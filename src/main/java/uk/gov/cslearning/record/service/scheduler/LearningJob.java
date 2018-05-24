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

    private static final Logger LOGGER = LoggerFactory.getLogger(LearningJob.class);

    private static final String COURSE_URI_FORMAT = "http://cslearning.gov.uk/courses/%s";

    private static final long[] NOTIFICATION_PERIODS = new long[]{30, 7, 1};

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
                    Optional<Notification> optionalNotification = notificationRepository.findFirstByIdentityUidAndCourseIdOrderBySentDesc(identity.getUid(), c.getId());
                    if (optionalNotification.isPresent()) {
                        Notification notification = optionalNotification.get();
                        if (notification.getNotificationType() == NotificationType.DAY) {
                            if (notification.getSent().isBefore(LocalDateTime.now().minusDays(1))) {
                                incompleteCoursesDay.add(c);
                            }
                        } else if (notification.getNotificationType() == NotificationType.WEEK) {
                            if (notification.getSent().isBefore(LocalDateTime.now().minusDays(7))) {
                                incompleteCoursesWeek.add(c);
                            }
                        }
                        if (notification.getNotificationType() == NotificationType.MONTH) {
                            if (notification.getSent().isBefore(LocalDateTime.now().minusDays(30))) {
                                incompleteCoursesMonth.add(c);
                            }
                        }
                    } else {
                        for (long days : NOTIFICATION_PERIODS) {
                            if (now.plusDays(days).isAfter(nextRequiredBy)) {
                                if (days == 1) {
                                    incompleteCoursesDay.add(c);
                                }
                                if (days == 7) {
                                    incompleteCoursesWeek.add(c);
                                }
                                if (days == 30) {
                                    incompleteCoursesMonth.add(c);
                                }
                            }
                        }
                    }
                }
            }

            if (!incompleteCoursesDay.isEmpty()) {
                sendNotifiyForPeriod(identity, incompleteCoursesDay, "1 day");
            }
            if (!incompleteCoursesWeek.isEmpty()) {
                sendNotifiyForPeriod(identity, incompleteCoursesWeek, "1 week");
            }
            if (!incompleteCoursesMonth.isEmpty()) {
                sendNotifiyForPeriod(identity, incompleteCoursesMonth, "1 month");
            }
        }
    }

    private void sendNotifiyForPeriod(Identity identity, List<Course> incompleteCourses, String period) throws NotificationClientException {
        StringBuilder requiredLearning = new StringBuilder();
        for (Course c : incompleteCourses) {
            requiredLearning.append(c.getTitle() + "\n");
        }
        notifyService.notify(identity.getUsername(), requiredLearning.toString(), govNotifyRequiredLearningDueTemplateId, period);
    }
}
