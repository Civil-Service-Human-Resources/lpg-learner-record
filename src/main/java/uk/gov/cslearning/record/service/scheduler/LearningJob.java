package uk.gov.cslearning.record.service.scheduler;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
import uk.gov.cslearning.record.service.CourseRefreshService;
import uk.gov.cslearning.record.service.NotifyService;
import uk.gov.cslearning.record.service.UserRecordService;
import uk.gov.cslearning.record.service.catalogue.Course;
import uk.gov.cslearning.record.service.catalogue.LearningCatalogueService;
import uk.gov.cslearning.record.service.identity.Identity;
import uk.gov.cslearning.record.service.identity.IdentityService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

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

    private static final long MINIMUM_DAY_PERIOD = 1;

    @Value("${govNotify.template.requiredLearningDue}")
    private String govNotifyRequiredLearningDueTemplateId;

    @Value("${govNotify.template.completedLearning}")
    private String govNotifyCompletedLearningTemplateId;

    private IdentityService identityService;

    private RegistryService registryService;

    private LearningCatalogueService learningCatalogueService;

    private NotifyService notifyService;

    private NotificationRepository notificationRepository;

    private UserRecordService userRecordService;

    private CourseRecordRepository courseRecordRepository;

    private CourseRefreshService courseRefreshService;

    private CourseNotificationJobHistoryRepository courseNotificationJobHistoryRepository;

    @Autowired
    public LearningJob(UserRecordService userRecordService,
            IdentityService identityService,
            RegistryService registryService,
            LearningCatalogueService learningCatalogueService,
            NotifyService notifyService,
            NotificationRepository notificationRepository,
            CourseRecordRepository courseRecordRepository,
            CourseRefreshService courseRefreshService,
            CourseNotificationJobHistoryRepository courseNotificationJobHistoryRepository) {
        this.userRecordService = userRecordService;
        this.identityService = identityService;
        this.registryService = registryService;
        this.learningCatalogueService = learningCatalogueService;
        this.notifyService = notifyService;
        this.notificationRepository = notificationRepository;
        this.courseRecordRepository = courseRecordRepository;
        this.courseRefreshService = courseRefreshService;
        this.courseNotificationJobHistoryRepository = courseNotificationJobHistoryRepository;
    }

    @Transactional
    public void sendLineManagerNotificationForCompletedLearning() throws HttpClientErrorException {
        LOGGER.info("Sending notifications for complete learning.");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime since = getSinceDate(courseNotificationJobHistoryRepository.findLastCompletedCoursesJobRecord(), now);

        CourseNotificationJobHistory courseNotificationJobHistory = new CourseNotificationJobHistory(CourseNotificationJobHistory.JobName.COMPLETED_COURSES_JOB.name(), now);
        courseNotificationJobHistoryRepository.save(courseNotificationJobHistory);

        courseRefreshService.refreshCoursesForATimePeriod(since);
        List<CourseRecord> completedCourseRecords = courseRecordRepository.findCompletedByLastUpdated(since);

        courseNotificationJobHistory.setDataAcquisition(LocalDateTime.now());
        courseNotificationJobHistoryRepository.save(courseNotificationJobHistory);

        completedCourseRecords.forEach(courseRecord ->
                registryService.getCivilServantByUid(courseRecord.getUserId())
                    .ifPresent(civilServant -> checkAndNotifyLineManager(civilServant, courseRecord, since)));

        courseNotificationJobHistory.setCompletedAt(LocalDateTime.now());
        courseNotificationJobHistoryRepository.save(courseNotificationJobHistory);
    }

    void checkAndNotifyLineManager(CivilServant civilServant, CourseRecord courseRecord, LocalDateTime completedDate) {
        LOGGER.debug("Notifying line manager of course completion for user {}, course id = {}", courseRecord.getUserId(), courseRecord.getCourseId());

        Optional<Notification> optionalNotification = notificationRepository.findFirstByIdentityUidAndCourseIdAndTypeOrderBySentDesc(courseRecord.getUserId(), courseRecord.getCourseId(), NotificationType.COMPLETE);
        boolean shouldSendNotification = optionalNotification.map(notification -> notification.sentBefore(completedDate))
            .orElse(true);

        if (shouldSendNotification) {
            String emailAddress = identityService.getEmailAddress(civilServant.getLineManagerUid());

            notifyService.notifyOnComplete(emailAddress, govNotifyCompletedLearningTemplateId, civilServant.getFullName(), emailAddress, courseRecord.getCourseTitle());

            Notification notification = new Notification(courseRecord.getCourseId(), courseRecord.getUserId(), NotificationType.COMPLETE);
            notificationRepository.save(notification);
        } else {
            LOGGER.info("User has already been sent notification");
        }
    }

    @Transactional
    public void sendReminderNotificationForIncompleteCourses() {
        CourseNotificationJobHistory courseNotificationJobHistory = new CourseNotificationJobHistory(CourseNotificationJobHistory.JobName.INCOMPLETED_COURSES_JOB.name(), LocalDateTime.now());
        courseNotificationJobHistoryRepository.save(courseNotificationJobHistory);

        Map<String, List<Course>> coursesGroupedByOrg = learningCatalogueService.getRequiredCoursesByDueDaysGroupedByOrg(NOTIFICATION_PERIOD_PARAM);
        courseNotificationJobHistory.setDataAcquisition(LocalDateTime.now());
        courseNotificationJobHistoryRepository.save(courseNotificationJobHistory);

        Map<String, NotificationCourseModule> coursesGroupedByUserId = groupCourseByUserId(coursesGroupedByOrg);

        Map<Long, List<Course>> incompleteCourses = new HashMap<>();
        LocalDate now = LocalDate.now();

        coursesGroupedByUserId.forEach((userId, notificationCourseModule) -> {
            for (Course course : notificationCourseModule.getCourses()) {
                Collection<CourseRecord> courseRecords = userRecordService.getUserRecord(userId, Lists.newArrayList(course.getId()));
                    LocalDate mostRecentlyCompleted = null;

                    for (CourseRecord courseRecord : courseRecords) {
                        LocalDateTime courseCompletionDate = courseRecord.getCompletionDate();
                        if (mostRecentlyCompleted == null || courseCompletionDate != null && mostRecentlyCompleted.isBefore(courseCompletionDate.toLocalDate())) {
                            mostRecentlyCompleted = courseCompletionDate.toLocalDate();
                        }
                    }

                    LocalDate nextRequiredBy = course.getNextRequiredBy(notificationCourseModule.getCivilServant(), mostRecentlyCompleted);
                    LOGGER.debug("Next required by for course {} is {}", course, nextRequiredBy);

                    if (nextRequiredBy != null) {
                        checkAndAdd(course, notificationCourseModule.getCivilServant().getIdentity(), nextRequiredBy, now, incompleteCourses);
                    }
                }
            for (Map.Entry<Long, List<Course>> entry : incompleteCourses.entrySet()) {
                sendNotificationForPeriod(notificationCourseModule.getCivilServant().getIdentity(), entry.getKey(), entry.getValue());
            }
        });

        courseNotificationJobHistory.setCompletedAt(LocalDateTime.now());
        courseNotificationJobHistoryRepository.save(courseNotificationJobHistory);
        LOGGER.info("Sending notifications complete");
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
                    .append("\n");
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
                    List<Course> presentCourses = addIncompletedCoursesCoursesForUser(coursesGroupedByUserId, courses, civilServant);
                    coursesGroupedByUserId.get(civilServant.getIdentity().getUid()).getCourses().addAll(presentCourses);
                }
            });
        });

        return coursesGroupedByUserId;
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

    private List<Course> addIncompletedCoursesCoursesForUser(Map<String, NotificationCourseModule> coursesGroupedByUserId, List<Course> courses, CivilServant civilServant) {
        List<Course> presentCourses = coursesGroupedByUserId.get(civilServant.getIdentity().getUid())
            .getCourses();
        List<String> presentCourseIds = presentCourses.stream()
            .map(Course::getId)
            .collect(Collectors.toList());
        courses.forEach(course -> {
            if (!presentCourseIds.contains(course.getId()) && !courseRecordRepository.findCompletedByCourseIdAndUserId(course.getId(), civilServant.getIdentity().getUid()).isPresent()) {
                presentCourses.add(course);
            }
        });

        return presentCourses;
    }

    private LocalDateTime getSinceDate(Optional<CourseNotificationJobHistory> courseNotificationJobHistory, LocalDateTime now) {
        if (courseNotificationJobHistory.isPresent()) {
            LocalDateTime startedAt = courseNotificationJobHistory.get().getStartedAt();
            long days = calculateDayDifference(Duration.between(startedAt, now).toDays());
            return LocalDateTime.now().minusDays(days);
        } else {
            return LocalDateTime.now().minusDays(1);
        }
    }

    private long calculateDayDifference(long days) {
        if (days == 0) {
            return MINIMUM_DAY_PERIOD;
        }

        return days;
    }
}
