package uk.gov.cslearning.record.service.scheduler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import uk.gov.cslearning.record.csrs.domain.CivilServant;
import uk.gov.cslearning.record.csrs.service.RegistryService;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.Notification;
import uk.gov.cslearning.record.domain.NotificationType;
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

@Component
public class LearningJob {

    private static final String DAY_PERIOD = "1 day";

    private static final String WEEK_PERIOD = "1 week";

    private static final String MONTH_PERIOD = "1 month";

    private static final Logger LOGGER = LoggerFactory.getLogger(LearningJob.class);

    private static final String COURSE_URI_FORMAT = "http://cslearning.gov.uk/courses/%s";

    private static final long[] NOTIFICATION_PERIODS = new long[]{1, 7, 30};

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

    @Autowired
    public LearningJob(UserRecordService userRecordService,
            IdentityService identityService,
            RegistryService registryService,
            LearningCatalogueService learningCatalogueService,
            NotifyService notifyService,
            NotificationRepository notificationRepository,
            CourseRecordRepository courseRecordRepository,
            CourseRefreshService courseRefreshService) {
        this.userRecordService = userRecordService;
        this.identityService = identityService;
        this.registryService = registryService;
        this.learningCatalogueService = learningCatalogueService;
        this.notifyService = notifyService;
        this.notificationRepository = notificationRepository;
        this.courseRecordRepository = courseRecordRepository;
        this.courseRefreshService = courseRefreshService;
    }

    @Transactional
    public void sendLineManagerNotificationForCompletedLearning() throws HttpClientErrorException {
        LOGGER.info("Sending notifications for complete learning.");

        LocalDateTime since = LocalDateTime.now().minusDays(10);
        courseRefreshService.refreshCoursesForATimePeriod(since);
        List<CourseRecord> completedCourseRecords = courseRecordRepository.findCompletedByLastUpdated(since);

        completedCourseRecords.forEach(courseRecord ->
                registryService.getCivilServantByUid(courseRecord.getUserId()).ifPresent(civilServant -> checkAndNotifyLineManager(civilServant, courseRecord, since)));
    }

    @Transactional
    public void sendReminderNotificationForIncompleteCourses() {
        Map<String, List<CourseRecord>> incompletedCourseRecordsGroupedByUid = courseRecordRepository.findIncompleted()
            .stream()
            .collect(Collectors.groupingBy(CourseRecord::getUserId));
        LocalDate now = LocalDate.now();

        for (Map.Entry<String, List<CourseRecord>> courseEntry : incompletedCourseRecordsGroupedByUid.entrySet()) {
            processIncompleteCoursesByIdentity(courseEntry.getKey(), courseEntry.getValue(), now);
        }

        LOGGER.info("Sending notifications complete");
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

    private void processIncompleteCoursesByIdentity(String userId, List<CourseRecord> courseRecords, LocalDate now) {
        Optional<Identity> fetchedIdentity = identityService.getIdentityByUid(userId);
        fetchedIdentity.ifPresent(identity -> processIncompleteCoursesByCivilServantAndSendNotifications(identity, courseRecords, now));
    }

    private void processIncompleteCoursesByCivilServantAndSendNotifications(Identity identity, List<CourseRecord> courseRecords, LocalDate now) {
        Optional<CivilServant> fetchedCivilServant = registryService.getCivilServantByUid(identity.getUid());

        fetchedCivilServant.ifPresent(civilServant -> {
            Map<Long, List<Course>> incompleteCourses = new HashMap<>();
            addValidIncompleteCoursesForNotifications(identity, civilServant, courseRecords, now, incompleteCourses);
            for (Map.Entry<Long, List<Course>> entry : incompleteCourses.entrySet()) {
                sendNotificationForPeriod(identity, entry.getKey(), entry.getValue());
            }
        });
    }

    private void addValidIncompleteCoursesForNotifications(Identity identity, CivilServant civilServant, List<CourseRecord> courseRecords, LocalDate now, Map<Long, List<Course>> incompleteCourses) {
        LocalDate mostRecentlyCompleted = null;

        for (CourseRecord courseRecord : courseRecords) {
            LocalDateTime courseCompletionDate = courseRecord.getCompletionDate();
            if (mostRecentlyCompleted == null || courseCompletionDate != null && mostRecentlyCompleted.isBefore(courseCompletionDate.toLocalDate())) {
                mostRecentlyCompleted = courseCompletionDate.toLocalDate();
            }

            Course course = learningCatalogueService.getCourse(courseRecord.getCourseId());
            LocalDate nextRequiredBy = course.getNextRequiredBy(civilServant, mostRecentlyCompleted);

            if (nextRequiredBy != null) {
                checkAndAdd(course, identity, nextRequiredBy, now, incompleteCourses);
            }
        }
    }
}
