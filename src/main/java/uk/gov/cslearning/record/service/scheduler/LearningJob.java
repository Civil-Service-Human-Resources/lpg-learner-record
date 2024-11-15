package uk.gov.cslearning.record.service.scheduler;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.csrs.domain.CivilServant;
import uk.gov.cslearning.record.csrs.service.RegistryService;
import uk.gov.cslearning.record.domain.CourseNotificationJobHistory;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.Notification;
import uk.gov.cslearning.record.domain.NotificationType;
import uk.gov.cslearning.record.dto.NotificationCourseModule;
import uk.gov.cslearning.record.repository.CourseNotificationJobHistoryRepository;
import uk.gov.cslearning.record.repository.CourseRecordRepository;
import uk.gov.cslearning.record.repository.NotificationRepository;
import uk.gov.cslearning.record.service.NotifyService;
import uk.gov.cslearning.record.service.UserRecordService;
import uk.gov.cslearning.record.service.catalogue.Course;
import uk.gov.cslearning.record.service.catalogue.LearningCatalogueService;
import uk.gov.cslearning.record.service.identity.Identity;
import uk.gov.cslearning.record.service.identity.IdentityService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class LearningJob {
    private static final String DAY_PERIOD = "1 day";

    private static final String WEEK_PERIOD = "1 week";

    private static final String MONTH_PERIOD = "1 month";

    private static final Logger LOGGER = LoggerFactory.getLogger(LearningJob.class);

    private static final List<Long> NOTIFICATION_PERIODS = ImmutableList.of(1L, 7L, 30L);

    private static final String NOTIFICATION_PERIOD_PARAM = NOTIFICATION_PERIODS.stream()
            .map(String::valueOf)
            .collect(Collectors.joining(","));


    @Value("${govNotify.template.requiredLearningDue}")
    private String govNotifyRequiredLearningDueTemplateId;

    private IdentityService identityService;

    private RegistryService registryService;

    private LearningCatalogueService learningCatalogueService;

    private NotifyService notifyService;

    private NotificationRepository notificationRepository;

    private UserRecordService userRecordService;

    private CourseRecordRepository courseRecordRepository;


    private CourseNotificationJobHistoryRepository courseNotificationJobHistoryRepository;

    @Autowired
    public LearningJob(UserRecordService userRecordService,
                       IdentityService identityService,
                       RegistryService registryService,
                       LearningCatalogueService learningCatalogueService,
                       NotifyService notifyService,
                       NotificationRepository notificationRepository,
                       CourseRecordRepository courseRecordRepository,
                       CourseNotificationJobHistoryRepository courseNotificationJobHistoryRepository) {
        this.userRecordService = userRecordService;
        this.identityService = identityService;
        this.registryService = registryService;
        this.learningCatalogueService = learningCatalogueService;
        this.notifyService = notifyService;
        this.notificationRepository = notificationRepository;
        this.courseRecordRepository = courseRecordRepository;
        this.courseNotificationJobHistoryRepository = courseNotificationJobHistoryRepository;
    }

    public void sendReminderNotificationForIncompleteCourses() {
        CourseNotificationJobHistory courseNotificationJobHistory = new CourseNotificationJobHistory(CourseNotificationJobHistory.JobName.INCOMPLETED_COURSES_JOB.name(), LocalDateTime.now());
        courseNotificationJobHistoryRepository.save(courseNotificationJobHistory);

        Map<String, List<Course>> coursesGroupedByOrg = learningCatalogueService.getRequiredCoursesByDueDaysGroupedByOrg(NOTIFICATION_PERIOD_PARAM);
        LOGGER.debug("Fetched {} incompleted records grouped by organisation", coursesGroupedByOrg.size());
        courseNotificationJobHistory.setDataAcquisition(LocalDateTime.now());
        courseNotificationJobHistoryRepository.save(courseNotificationJobHistory);

        Map<String, NotificationCourseModule> coursesGroupedByUserId = groupCourseByUserId(coursesGroupedByOrg);
        LOGGER.debug("{} users have incompleted courses", coursesGroupedByUserId.size());

        coursesGroupedByUserId.forEach((userId, notificationCourseModule) ->
                identityService.getIdentityByUid(userId)
                        .ifPresent(identity -> processGroupedCoursesAndSendNotifications(identity, notificationCourseModule, LocalDate.now())));

        courseNotificationJobHistory.setCompletedAt(LocalDateTime.now());
        courseNotificationJobHistoryRepository.save(courseNotificationJobHistory);
    }

    private void processGroupedCoursesAndSendNotifications(Identity identity, NotificationCourseModule notificationCourseModule, LocalDate now) {
        LOGGER.debug("Processing incomplete courses for user: {}", identity.getUsername());
        Map<Long, List<Course>> incompleteCourses = new HashMap<>();

        for (Course course : notificationCourseModule.getCourses()) {
            Collection<CourseRecord> courseRecords = userRecordService.getUserRecord(identity.getUid(), Lists.newArrayList(course.getId()));
            LocalDate mostRecentlyCompleted = null;
            for (CourseRecord courseRecord : courseRecords) {
                LocalDateTime courseCompletionDate = courseRecord.getCompletionDate();
                if (courseCompletionDate != null && (mostRecentlyCompleted == null || mostRecentlyCompleted.isBefore(courseCompletionDate.toLocalDate()))) {
                    mostRecentlyCompleted = courseCompletionDate.toLocalDate();
                }
            }
            LocalDate nextRequiredBy = course.getNextRequiredBy(notificationCourseModule.getCivilServant(), mostRecentlyCompleted);
            LOGGER.debug("Next required by for course {} is {}", course, nextRequiredBy);

            if (nextRequiredBy != null) {
                checkAndAdd(course, identity, nextRequiredBy, now, incompleteCourses);
            }
        }

        for (Map.Entry<Long, List<Course>> entry : incompleteCourses.entrySet()) {
            sendNotificationForPeriod(identity, entry.getKey(), entry.getValue());
        }
    }

    void checkAndAdd(Course course, Identity identity, LocalDate nextRequiredBy, LocalDate now, Map<Long, List<Course>> incompleteCourses) {
        if (nextRequiredBy.isBefore(now)) {
            return;
        }
        for (long notificationPeriod : NOTIFICATION_PERIODS) {
            LocalDate nowPlusNotificationPeriod = now.plusDays(notificationPeriod);
            if (nowPlusNotificationPeriod.isAfter(nextRequiredBy) || nowPlusNotificationPeriod.isEqual(nextRequiredBy)) {
                Optional<Notification> optionalNotification = notificationRepository.findFirstByIdentityUidAndCourseIdAndTypeOrderBySentDesc(identity.getUid(), course.getId(), NotificationType.REMINDER);
                if (!optionalNotification.isPresent() || Period.between(optionalNotification.get().getSent().toLocalDate(), now).getDays() > notificationPeriod) {
                    List<Course> incompleteCoursesForPeriod = incompleteCourses.computeIfAbsent(notificationPeriod, key -> new ArrayList<>());
                    incompleteCoursesForPeriod.add(course);
                }
                break;
            }
        }
    }

    void sendNotificationForPeriod(Identity identity, Long period, List<Course> courses) {
        StringBuilder requiredLearning = new StringBuilder();
        for (Course c : courses) {
            requiredLearning
                    .append(c.getTitle())
                    .append(System.lineSeparator());
        }

        String periodText;
        switch (period.intValue()) {
            case 1:
                periodText = DAY_PERIOD;
                break;
            case 7:
                periodText = WEEK_PERIOD;
                break;
            default:
                periodText = MONTH_PERIOD;
                break;
        }

        notifyService.notifyForIncompleteCourses(identity.getUsername(), requiredLearning.toString(), govNotifyRequiredLearningDueTemplateId, periodText);

        for (Course course : courses) {
            Notification notification = new Notification(course.getId(), identity.getUid(), NotificationType.REMINDER);
            notificationRepository.save(notification);
        }
    }

    private Map<String, NotificationCourseModule> groupCourseByUserId(Map<String, List<Course>> coursesGroupedByOrganisation) {
        Map<String, NotificationCourseModule> coursesGroupedByUserId = new HashMap<>();

        coursesGroupedByOrganisation.forEach((orgCode, courses) -> {
            List<CivilServant> civilServants = registryService.getCivilServantsByOrgCode(orgCode);
            civilServants.forEach(civilServant -> {
                if (!coursesGroupedByUserId.containsKey(civilServant.getIdentity().getUid())) {
                    coursesGroupedByUserId.putIfAbsent(civilServant.getIdentity().getUid(), new NotificationCourseModule(civilServant, filterCompletedCourses(courses, civilServant.getIdentity())));
                } else {
                    List<Course> presentCourses = coursesGroupedByUserId.get(civilServant.getIdentity().getUid())
                            .getCourses();
                    addCourseIfDoesNotExist(courses, presentCourses, civilServant.getIdentity());
                }
            });
        });

        return coursesGroupedByUserId;
    }

    private void addCourseIfDoesNotExist(List<Course> courses, List<Course> presentCourses, Identity identity) {
        List<Course> filteredCompletedCourses = filterCompletedCourses(courses, identity);

        List<String> presentCoursesIds = presentCourses.parallelStream()
                .map(Course::getId)
                .collect(Collectors.toList());

        filteredCompletedCourses.forEach(filteredCompletedCourse -> {
            if (!presentCoursesIds.contains(filteredCompletedCourse.getId())) {
                presentCourses.add(filteredCompletedCourse);
                presentCoursesIds.add(filteredCompletedCourse.getId());
            }
        });
    }

    private List<Course> filterCompletedCourses(List<Course> courses, Identity identity) {
        List<Course> incompletedCourses = new ArrayList<>();
        courses.forEach(course -> {
            Optional<CourseRecord> courseRecord = courseRecordRepository.findCompletedByCourseIdAndUserId(course.getId(), identity.getUid());
            if (!courseRecord.isPresent()) {
                incompletedCourses.add(course);
            }
        });

        return incompletedCourses;
    }

}
