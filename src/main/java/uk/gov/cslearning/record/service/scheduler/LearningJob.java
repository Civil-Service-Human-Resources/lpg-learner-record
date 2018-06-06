package uk.gov.cslearning.record.service.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.Notification;
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
    public void sendNotificationForIncompleteCourses() throws NotificationClientException {
        Collection<Identity> identities = identityService.listAll();

        for (Identity identity : identities) {
            LOGGER.debug("Got identity with uid {}", identity.getUid());

            CivilServant civilServant = registryService.getCivilServantByUid(identity.getUid());

            List<Course> courses = learningCatalogueService.getRequiredCoursesByDepartmentCode(civilServant.getDepartmentCode());
            Map<Long, List<Course>> incompleteCourses = new HashMap<>();
            LocalDate now = LocalDate.now();

            for (Course course : courses) {
                Collection<CourseRecord> courseRecords = userRecordService.getUserRecord(identity.getUid(), String.format(COURSE_URI_FORMAT, course.getId()));
                LocalDate mostRecentlyCompleted = null;

                for (CourseRecord courseRecord : courseRecords) {
                    LocalDateTime courseCompletionDate = courseRecord.getCompletionDate();
                    if (mostRecentlyCompleted == null || courseCompletionDate != null && mostRecentlyCompleted.isBefore(courseCompletionDate.toLocalDate())) {
                        mostRecentlyCompleted = courseCompletionDate.toLocalDate();
                    }
                }

                LocalDate nextRequiredBy = course.getNextRequiredBy(civilServant, mostRecentlyCompleted);
                LOGGER.debug("Next required by for course is {}", nextRequiredBy);

                if (nextRequiredBy != null) {
                    checkAndAdd(course, identity, nextRequiredBy, now, incompleteCourses);
                }
            }
            for (Map.Entry<Long, List<Course>> entry : incompleteCourses.entrySet()) {
                sendNotificationForPeriod(identity, entry.getKey(), entry.getValue());
            }
        }
    }

    void checkAndAdd(Course course, Identity identity, LocalDate nextRequiredBy, LocalDate now, Map<Long, List<Course>> incompleteCourses) {
        for (long notificationPeriod : NOTIFICATION_PERIODS) {
            if (now.plusDays(notificationPeriod).isAfter(nextRequiredBy)) {
                Optional<Notification> optionalNotification = notificationRepository.findFirstByIdentityUidAndCourseIdOrderBySentDesc(identity.getUid(), course.getId());
                if (!optionalNotification.isPresent() || Period.between(optionalNotification.get().getSent().toLocalDate(), now).getDays() > notificationPeriod) {
                    List<Course> incompleteCoursesForPeriod = incompleteCourses.computeIfAbsent(notificationPeriod, key -> new ArrayList<>());
                    incompleteCoursesForPeriod.add(course);
                }
                break;
            }
        }
    }

    void sendNotificationForPeriod(Identity identity, Long period, List<Course> courses) throws NotificationClientException {
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

        notifyService.notify(identity.getUsername(), requiredLearning.toString(), govNotifyRequiredLearningDueTemplateId, periodText);

        for (Course course : courses) {
            Notification notification = new Notification(course.getId(), identity.getUid());
            notificationRepository.save(notification);
        }
    }
}
