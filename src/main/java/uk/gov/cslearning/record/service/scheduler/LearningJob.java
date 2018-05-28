package uk.gov.cslearning.record.service.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.Notification;
import uk.gov.cslearning.record.domain.NotificationType;
import uk.gov.cslearning.record.repository.NotificationRepository;
import uk.gov.cslearning.record.service.CivilServant;
import uk.gov.cslearning.record.service.NotifyService;
import uk.gov.cslearning.record.service.RegistryService;
import uk.gov.cslearning.record.service.UserRecordService;
import uk.gov.cslearning.record.service.catalogue.Course;
import uk.gov.cslearning.record.service.catalogue.LearningCatalogueService;
import uk.gov.cslearning.record.service.identity.Identity;
import uk.gov.cslearning.record.service.identity.IdentityService;
import uk.gov.service.notify.NotificationClientException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
public class LearningJob {

    public static final String DAY_PERIOD = "1 day";
    public static final String WEEK_PERIOD = "1 week";
    public static final String MONTH_PERIOD = "1 month";
    public static final int WEEK_IN_DAYS = 7;
    public static final int MONTH_IN_DAYS = 30;
    public static final int DAY_IN_DAYS = 1;
    private static final Logger LOGGER = LoggerFactory.getLogger(LearningJob.class);
    private static final String COURSE_URI_FORMAT = "http://cslearning.gov.uk/courses/%s";
    private static final long[] NOTIFICATION_PERIODS = new long[]{1, 7, 30};
    @Value("${govNotify.template.requiredLearningDue}")
    private String govNotifyRequiredLearningDueTemplateId;

    private IdentityService identityService;

    private RegistryService registryService;

    private LearningCatalogueService learningCatalogueService;

    private NotifyService notifyService;

    private NotificationRepository notificationRepository;

    @Autowired
    private UserRecordService userRecordService;

    @Autowired
    public LearningJob(IdentityService identityService, RegistryService registryService, LearningCatalogueService learningCatalogueService, NotifyService notifyService, NotificationRepository notificationRepository) {
        this.identityService = identityService;
        this.registryService = registryService;
        this.learningCatalogueService = learningCatalogueService;
        this.notifyService = notifyService;
        this.notificationRepository = notificationRepository;
    }

    public void sendNotificationForIncompleteCourses() throws NotificationClientException {
        Collection<Identity> identities = identityService.listAll();

        for (Identity identity : identities) {
            LOGGER.info("Got identity with uid {} and username {}", identity.getUid(), identity.getUsername());
            CivilServant civilServant = registryService.getCivilServantByUid(identity.getUid());

            List<Course> courses = learningCatalogueService.getRequiredCoursesByDepartmentCode(civilServant.getDepartmentCode());
            List<Course> incompleteCoursesDay = new ArrayList<>();
            List<Course> incompleteCoursesWeek = new ArrayList<>();
            List<Course> incompleteCoursesMonth = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now();

            for (Course c : courses) {
                Collection<CourseRecord> courseRecords = userRecordService.getUserRecord(identity.getUid(), String.format(COURSE_URI_FORMAT, c.getId()));
                LocalDateTime mostRecentlyCompleted = null;

                for (CourseRecord courseRecord : courseRecords) {
                    LocalDateTime courseCompletionDate = courseRecord.getCompletionDate();
                    if (mostRecentlyCompleted == null || courseCompletionDate != null && mostRecentlyCompleted.isBefore(courseCompletionDate)) {
                        mostRecentlyCompleted = courseCompletionDate;
                    }
                }

                LocalDateTime nextRequiredBy = c.getNextRequiredBy(civilServant, mostRecentlyCompleted);
                LOGGER.debug("Next required by for course is {}", nextRequiredBy);
                if (nextRequiredBy != null) {
                    /*
                     * Matt - If a notification has been sent before,
                     * look at the last notification sent for this identity/course and add courses to notify list accordingly
                     */
                    Optional<Notification> optionalNotification = notificationRepository.findFirstByIdentityUidAndCourseIdOrderBySentDesc(identity.getUid(), c.getId());
                    if (optionalNotification.isPresent()) {
                        Notification notification = optionalNotification.get();
                        addIncompleteCoursesToList(incompleteCoursesDay, incompleteCoursesWeek, incompleteCoursesMonth, c, notification, now);
                    } else {
                        addToIncompleteCoursesIfNotificationIsNew(incompleteCoursesDay, incompleteCoursesWeek, incompleteCoursesMonth, now, c, nextRequiredBy);
                    }
                }
            }
            sendNotifyForIncompleteCourses(identity, incompleteCoursesDay, incompleteCoursesWeek, incompleteCoursesMonth);
        }
    }

    /*
     * Matt - This will be called when a notification already exists for this identity/course.
     * Currently we have 3 notification types, day/week/month,
     * what happens if a course is due within a day, and in this case they complete it. Then next year when the course is due again,
     * The notification for this course will currently show of type DAY, even though it is from last years reminder.
     * So we either need to set another type or
     * we can use this method to ensure we are send the correct notification.
     *
     * It will also be used for helping to manage when a course passes the month notification point, into the week or day period.
     * For example in the type == NotificationType.WEEK block, if the last notification has been sent after now.minusDays(1),
     * it is ready for the DAY notification, so we should add it to the incompleteCoursesDay list.
     *
     */
    protected void addIncompleteCoursesToList(List<Course> incompleteCoursesDay, List<Course> incompleteCoursesWeek, List<Course> incompleteCoursesMonth, Course c, Notification notification, LocalDateTime now) {
        if (notification.getNotificationType() == NotificationType.DAY) {
            if (notification.getSent().isBefore(now.minusDays(DAY_IN_DAYS))) {
                incompleteCoursesDay.add(c);
            }
        } else if (notification.getNotificationType() == NotificationType.WEEK) {
            if (notification.getSent().isBefore(now.minusDays(WEEK_IN_DAYS))) {
                incompleteCoursesWeek.add(c);
            } else if (notification.getSent().isAfter(now.minusDays(DAY_IN_DAYS))) {
                incompleteCoursesDay.add(c);
            }
        } else if (notification.getNotificationType() == NotificationType.MONTH) {
            if (notification.getSent().isBefore(now.minusDays(MONTH_IN_DAYS))) {
                incompleteCoursesMonth.add(c);
            } else if (notification.getSent().isAfter(now.minusDays(DAY_IN_DAYS))) {
                incompleteCoursesDay.add(c);
            } else if (notification.getSent().isAfter(now.minusDays(WEEK_IN_DAYS))) {
                incompleteCoursesWeek.add(c);
            }
        }
    }

    /*
    * Matt - Just extracting this out to a testable method
    */
    protected void sendNotifyForIncompleteCourses(Identity identity, List<Course> incompleteCoursesDay, List<Course> incompleteCoursesWeek, List<Course> incompleteCoursesMonth) throws NotificationClientException {
        if (!incompleteCoursesDay.isEmpty()) {
            sendNotifiyForPeriod(identity, incompleteCoursesDay, DAY_PERIOD);
        }
        if (!incompleteCoursesWeek.isEmpty()) {
            sendNotifiyForPeriod(identity, incompleteCoursesWeek, WEEK_PERIOD);
        }
        if (!incompleteCoursesMonth.isEmpty()) {
            sendNotifiyForPeriod(identity, incompleteCoursesMonth, MONTH_PERIOD);
        }
    }

    /*
    * Matt - This block gets called if a notification has never existed for this identity/course.
    * Think of it as the first time a user gets reminded of a course.
    * Adding breaks to ensure that the user doesn't get 3 emails for the same course.
    * I know we spoke to Rich about this,
    * but i figured it shouldnt be too much work just to get the one email per period logic worked out
    * */
    public void addToIncompleteCoursesIfNotificationIsNew(List<Course> incompleteCoursesDay, List<Course> incompleteCoursesWeek, List<Course> incompleteCoursesMonth, LocalDateTime now, Course c, LocalDateTime nextRequiredBy) {
        for (long days : NOTIFICATION_PERIODS) {
            if (now.plusDays(days).isAfter(nextRequiredBy)) {
                if (days == 1) {
                    incompleteCoursesDay.add(c);
                    break;
                } else if (days == 7) {
                    incompleteCoursesWeek.add(c);
                    break;
                } else if (days == 30) {
                    incompleteCoursesMonth.add(c);
                }
            }
        }
    }

    protected void sendNotifiyForPeriod(Identity identity, List<Course> incompleteCourses, String period) throws NotificationClientException {
        StringBuilder requiredLearning = new StringBuilder();
        for (Course c : incompleteCourses) {
            requiredLearning.append(c.getTitle() + "\n");
        }
        notifyService.notify(identity.getUsername(), requiredLearning.toString(), govNotifyRequiredLearningDueTemplateId, period);
        // Matt - need to update db as well
    }
}
