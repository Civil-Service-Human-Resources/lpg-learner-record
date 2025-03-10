package uk.gov.cslearning.record.service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.config.jobs.LearningRemindersConfig;
import uk.gov.cslearning.record.csrs.service.RegistryService;
import uk.gov.cslearning.record.domain.CourseNotificationJobHistory;
import uk.gov.cslearning.record.domain.CourseRecords;
import uk.gov.cslearning.record.domain.Notification;
import uk.gov.cslearning.record.domain.NotificationType;
import uk.gov.cslearning.record.notifications.dto.IMessageParams;
import uk.gov.cslearning.record.notifications.service.NotificationService;
import uk.gov.cslearning.record.repository.CourseNotificationJobHistoryRepository;
import uk.gov.cslearning.record.repository.NotificationRepository;
import uk.gov.cslearning.record.service.CourseRecordService;
import uk.gov.cslearning.record.service.MessageService;
import uk.gov.cslearning.record.service.catalogue.LearningCatalogueService;
import uk.gov.cslearning.record.service.catalogue.RequiredCourse;
import uk.gov.cslearning.record.service.identity.IdentitiesService;
import uk.gov.cslearning.record.util.UtilService;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class LearningJob {

    private final LearningRemindersConfig learningRemindersConfig;
    private final UtilService utilService;
    private final IdentitiesService identityService;
    private final RegistryService registryService;
    private final LearningCatalogueService learningCatalogueService;
    private final MessageService messageService;
    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;
    private final CourseRecordService courseRecordService;
    private final CourseNotificationJobHistoryRepository courseNotificationJobHistoryRepository;

    private LearningJobCourseDataMap getRequiredLearningJobMap() {
        LocalDateTime now = utilService.getNowDateTime();
        log.info("Fetching mandatory learning");
        List<LearningNotificationPeriod> deadlinePeriods = learningRemindersConfig.getReminderPeriods();
        Map<String, List<RequiredCourse>> coursesGroupedByOrg = learningCatalogueService
                .getRequiredCoursesByDueDaysGroupedByOrg(deadlinePeriods.stream()
                        .map(LearningNotificationPeriod::getDays).toList());
        log.info("Fetched {} courses across {} departments", coursesGroupedByOrg.values().size(), coursesGroupedByOrg.keySet().size());
        LearningJobCourseDataMap courseData = LearningJobCourseDataMap.create(deadlinePeriods);
        coursesGroupedByOrg.forEach((depCode, courses) -> courses.forEach(course -> course.getLearningPeriod(depCode)
                .ifPresent(learningPeriod -> {
                    for (LearningNotificationPeriod deadlinePeriod : deadlinePeriods) {
                        if (learningPeriod.getEndDate().minus(deadlinePeriod.getDays(), ChronoUnit.DAYS).equals(now.toLocalDate())) {
                            courseData.addCourseAndDepartment(deadlinePeriod, course, depCode, learningPeriod);
                        }
                    }
                })));
        return courseData;
    }

    public void sendReminderNotificationForIncompleteCourses() {
        LocalDateTime now = utilService.getNowDateTime();
        log.info("Starting reminder for incomplete learning job");
        CourseNotificationJobHistory courseNotificationJobHistory = new CourseNotificationJobHistory(CourseNotificationJobHistory.JobName.INCOMPLETED_COURSES_JOB.name(), now);
        courseNotificationJobHistoryRepository.save(courseNotificationJobHistory);
        LearningJobCourseDataMap requiredLearningData = getRequiredLearningJobMap();
        log.info("Fetching civil servants for {}", requiredLearningData);
        List<String> courseIds = requiredLearningData.getCourseIds().stream().toList();
        if (!courseIds.isEmpty()) {
            Map<String, List<CourseRecords>> civilServants = new HashMap<>();
            for (String orgCode : requiredLearningData.getOrgCodes()) {
                List<CourseRecords> courseRecords = groupCourseRecordsByUserId(orgCode, courseIds);
                civilServants.put(orgCode, courseRecords);
            }
            log.info("Fetched {} civil servants across {} departments for course deadlines", civilServants.values().size(), requiredLearningData.keySet().size());
            courseNotificationJobHistory.setDataAcquisition(now);
            courseNotificationJobHistoryRepository.save(courseNotificationJobHistory);
            List<IMessageParams> reminders = new ArrayList<>();
            List<Notification> notifications = new ArrayList<>();
            Map<String, String> uidsToEmails = new HashMap<>();
            List<String> notificationsSentToday = notificationRepository.findAllBySentAfter(now.with(LocalTime.MIN))
                    .stream().map(n -> String.format("%s-%s", n.getCourseId(), n.getIdentityUid())).toList();
            for (LearningJobCourseData courseData : requiredLearningData.values()) {
                log.info("Processing course data: {}", courseData.toString());
                Map<String, List<CourseTitleWithId>> uidsToMissingCoursesMap = courseData.getUidsToMissingCourses(civilServants);
                log.info("Found {} UIDs that haven't completed courses", uidsToMissingCoursesMap.size());
                List<String> uidsNotAlreadyFetched = uidsToMissingCoursesMap.keySet().stream().filter(uid -> !uidsToEmails.containsKey(uid)).toList();
                uidsToEmails.putAll(identityService.getUidToEmailMap(uidsNotAlreadyFetched));
                uidsToMissingCoursesMap.forEach((uid, courses) -> {
                    List<String> courseTitles = new ArrayList<>();
                    for (CourseTitleWithId course : courses) {
                        if (!notificationsSentToday.contains(String.format("%s-%s", course.getCourseId(), uid))) {
                            notifications.add(new Notification(course.getCourseId(), uid, now, NotificationType.REMINDER));
                            courseTitles.add(course.getCourseTitle());
                        }
                    }
                    if (!courseTitles.isEmpty()) {
                        String email = uidsToEmails.get(uid);
                        if (email != null) {
                            IMessageParams messageDto = messageService.createIncompleteCoursesMessage(email, courseTitles, courseData.getPeriod().getText());
                            reminders.add(messageDto);
                        } else {
                            log.warn(String.format("Email for UID %s was not found", uid));
                        }
                    }
                });
            }
            notificationRepository.saveAll(notifications);
            log.info("Sending {} email reminders", reminders.size());
            notificationService.send(reminders);
        } else {
            log.info("No courses found, skipping");
        }
        courseNotificationJobHistory.setCompletedAt(now);
        courseNotificationJobHistoryRepository.save(courseNotificationJobHistory);
    }

    private List<CourseRecords> groupCourseRecordsByUserId(String orgCode, List<String> courseIds) {
        List<String> uids = registryService.getCivilServantsByOrgCode(orgCode).stream().map(cs -> cs.getIdentity().getUid()).toList();
        log.info("Fetched {} uids for department {}", uids.size(), orgCode);
        return courseRecordService.getCourseRecords(uids, courseIds);
    }
}
