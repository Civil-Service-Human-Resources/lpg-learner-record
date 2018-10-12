package uk.gov.cslearning.record.service.scheduler;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import uk.gov.cslearning.record.csrs.domain.CivilServant;
import uk.gov.cslearning.record.csrs.service.RegistryService;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.Notification;
import uk.gov.cslearning.record.domain.NotificationType;
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

    @Autowired
    public LearningJob(UserRecordService userRecordService, IdentityService identityService, RegistryService registryService, LearningCatalogueService learningCatalogueService, NotifyService notifyService, NotificationRepository notificationRepository) {
        this.userRecordService = userRecordService;
        this.identityService = identityService;
        this.registryService = registryService;
        this.learningCatalogueService = learningCatalogueService;
        this.notifyService = notifyService;
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public void sendLineManagerNotificationForCompletedLearning() throws HttpClientErrorException {
        LOGGER.info("Sending notifications for complete learning.");

        Collection<Identity> identities = identityService.listAll();

        for (Identity identity : identities) {
            LOGGER.debug("Got identity {}", identity);
            Optional<CivilServant> optionalCivilServant = registryService.getCivilServantByUid(identity.getUid());

            if (optionalCivilServant.isPresent()) {

                CivilServant civilServant = optionalCivilServant.get();

                if (civilServant.getLineManagerUid() == null) {
                    LOGGER.debug("User {} has no line manager, skipping", identity);
                    continue;
                }

                List<Course> courses = learningCatalogueService.getRequiredCoursesByDepartmentCode(civilServant.getOrganisationalUnit().getCode());

                LOGGER.debug("Found {} required courses", courses.size());

                for (Course course : courses) {
                    Collection<CourseRecord> courseRecords = userRecordService.getUserRecord(identity.getUid(), Lists.newArrayList(course.getId()));
                    for (CourseRecord courseRecord : courseRecords) {
                        LOGGER.debug("Course complete: {}", courseRecord.isComplete());
                        if (courseRecord.isComplete()) {
                            checkAndNotifyLineManager(civilServant, identity, course, courseRecord.getCompletionDate());
                        }
                    }
                }
            } else {
                LOGGER.info("Identity {} has no profile, skipping", identity);
            }
        }
    }

    void checkAndNotifyLineManager(CivilServant civilServant, Identity identity, Course course, LocalDateTime completedDate) {
        LOGGER.debug("Notifying line manager of course completion for user {}, course id = {}", identity, course);

        Optional<Notification> optionalNotification = notificationRepository.findFirstByIdentityUidAndCourseIdAndTypeOrderBySentDesc(identity.getUid(), course.getId(), NotificationType.COMPLETE);
        boolean shouldSendNotification = optionalNotification.map(notification -> notification.sentBefore(completedDate))
                .orElse(true);

        if (shouldSendNotification) {
            Optional<CivilServant> optionalLineManager = registryService.getCivilServantByUid(civilServant.getLineManagerUid());
            if (optionalLineManager.isPresent()) {
                CivilServant lineManager = optionalLineManager.get();

                notifyService.notifyOnComplete(civilServant.getLineManagerEmailAddress(), govNotifyCompletedLearningTemplateId, civilServant.getFullName(), lineManager.getFullName(), course.getTitle());

                Notification notification = new Notification(course.getId(), identity.getUid(), NotificationType.COMPLETE);
                notificationRepository.save(notification);
            } else {
                LOGGER.error("User has line manager but line manager does not exist!");
            }
        } else {
            LOGGER.debug("Notification already sent.");
        }
    }

    @Transactional
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
}
