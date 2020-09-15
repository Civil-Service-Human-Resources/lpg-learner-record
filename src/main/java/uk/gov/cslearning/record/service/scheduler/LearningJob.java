package uk.gov.cslearning.record.service.scheduler;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import uk.gov.cslearning.record.csrs.domain.CivilServant;
import uk.gov.cslearning.record.csrs.service.RegistryService;
import uk.gov.cslearning.record.domain.CourseNotificationJobHistory;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.Notification;
import uk.gov.cslearning.record.domain.NotificationType;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;

@Component
public class LearningJob {

    private static final String DAY_PERIOD = "1 day";

    private static final String WEEK_PERIOD = "1 week";

    private static final String MONTH_PERIOD = "1 month";

    private static final Logger LOGGER = LoggerFactory.getLogger(LearningJob.class);

    private static final String COURSE_URI_FORMAT = "http://cslearning.gov.uk/courses/%s";

    private static final long[] NOTIFICATION_PERIODS = new long[]{1, 7, 30};

    private static final long MINIMUM__DAY_PERIOD = 1;

    private static final long MAXIMUM__DAY_PERIOD = 7;

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

    public void learnerRecordRefresh() {
        LOGGER.info("Doing Learner Record Refresh");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime since = getSinceDate(courseNotificationJobHistoryRepository.findLastCompletedCoursesJobRecord(), now);

        CourseNotificationJobHistory courseNotificationJobHistory = new CourseNotificationJobHistory(CourseNotificationJobHistory.JobName.LEARNER_RECORD_REFRESH.name(), now);
        courseNotificationJobHistoryRepository.save(courseNotificationJobHistory);

        int refreshCount = courseRefreshService.refreshCoursesForATimePeriod(since);
        LOGGER.info("Learner Record Refresh updated {} record(s)", refreshCount);
    }

    public void sendLineManagerNotificationForCompletedLearning() throws HttpClientErrorException {
        LOGGER.info("Sending notifications for complete learning.");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime since = getSinceDate(courseNotificationJobHistoryRepository.findLastCompletedCoursesJobRecord(), now);

        CourseNotificationJobHistory courseNotificationJobHistory = new CourseNotificationJobHistory(CourseNotificationJobHistory.JobName.COMPLETED_COURSES_JOB.name(), now);
        courseNotificationJobHistoryRepository.save(courseNotificationJobHistory);

        List<CourseRecord> completedCourseRecords = courseRecordRepository.findCompletedByLastUpdated(since);
        
        LOGGER.info("Found {} completed course records", completedCourseRecords.size());

        courseNotificationJobHistory.setDataAcquisition(LocalDateTime.now());
        courseNotificationJobHistoryRepository.save(courseNotificationJobHistory);

        completedCourseRecords.parallelStream().forEach(courseRecord ->
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
    
    public void sendReminderNotificationForIncompleteCourses() {
        Collection<Identity> identities = identityService.listAll();

        for (Identity identity : identities) {
            LOGGER.debug("Got identity with uid {} and email {}", identity.getUid(), identity.getUsername());

            Optional<CivilServant> optionalCivilServant = registryService.getCivilServantByUid(identity.getUid());
            if (optionalCivilServant.isPresent()) {
                CivilServant civilServant = optionalCivilServant.get();
                List<Course> courses = learningCatalogueService.getRequiredCoursesByDepartmentCode(civilServant.getOrganisationalUnit().getCode());
                Map<Long, List<Course>> incompleteCourses = new HashMap<>();
                LocalDate now = LocalDate.now();

                for (Course course : courses) {
                    Collection<CourseRecord> courseRecords = userRecordService.getUserRecord(identity.getUid(), Lists.newArrayList(course.getId()));
                    if (!course.isComplete(courseRecords)) {
                        LocalDate mostRecentlyCompleted = null;

                        for (CourseRecord courseRecord : courseRecords) {
                            LocalDateTime courseCompletionDate = courseRecord.getCompletionDate();
                            if (mostRecentlyCompleted == null || courseCompletionDate != null && mostRecentlyCompleted.isBefore(courseCompletionDate.toLocalDate())) {
                                mostRecentlyCompleted = courseCompletionDate.toLocalDate();
                            }
                        }

                        LocalDate nextRequiredBy = course.getNextRequiredBy(civilServant, mostRecentlyCompleted);
                        LOGGER.debug("Next required by for course {} is {}", course, nextRequiredBy);

                        if (nextRequiredBy != null) {
                            checkAndAdd(course, identity, nextRequiredBy, now, incompleteCourses);
                        }
                    }
                }
                for (Map.Entry<Long, List<Course>> entry : incompleteCourses.entrySet()) {
                    sendNotificationForPeriod(identity, entry.getKey(), entry.getValue());
                }
            }
        }
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

    private LocalDateTime getSinceDate(Optional<CourseNotificationJobHistory> courseNotificationJobHistory, LocalDateTime now) {
        if (courseNotificationJobHistory.isPresent()) {
            return courseNotificationJobHistory.get().getDataAcquisition();
        } else {
            return LocalDateTime.now().minusDays(1);
        }
    }
}
