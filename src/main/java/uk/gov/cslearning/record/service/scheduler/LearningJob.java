package uk.gov.cslearning.record.service.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.Notification;
import uk.gov.cslearning.record.domain.State;
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

    @Value("${govNotify.template.completedLearning}")
    private String govNotifyCompletedLearningTemplateId;

    private IdentityService identityService;

    private RegistryService registryService;

    private LearningCatalogueService learningCatalogueService;

    private NotifyService notifyService;

    private NotificationRepository notificationRepository;

    private UserRecordService userRecordService;

    private static final String COMPLETED = "COMPLETED";

    @Autowired
    public LearningJob(UserRecordService userRecordService, IdentityService identityService, RegistryService registryService, LearningCatalogueService learningCatalogueService, NotifyService notifyService, NotificationRepository notificationRepository) {
        this.userRecordService = userRecordService;
        this.identityService = identityService;
        this.registryService = registryService;
        this.learningCatalogueService = learningCatalogueService;
        this.notifyService = notifyService;
        this.notificationRepository = notificationRepository;
    }

    private void CheckAndNotifyLineManager(CivilServant civilServant, Identity identity,Course course, LocalDateTime completedDate) throws NotificationClientException {

        if (civilServant.getLineManagerEmail() == null) {
            LOGGER.error("User has no line manager!");
        } else {
            Boolean sendMail = false;
            Optional<Notification> optionalNotification = notificationRepository.findFirstByIdentityUidAndCourseIdAndNotificationType(identity.getUid(),course.getId(),COMPLETED);
            System.out.println(course.getId()+" "+identity.getUid()+" "+COMPLETED);
            Notification notification = new Notification(course.getId(), identity.getUid(), COMPLETED);
            System.out.println(notification.toString());

            if (!optionalNotification.isPresent()) {
                sendMail = true;
            } else {
                notification = optionalNotification.get();
                System.out.println("NOTIFICATION FOUND: "+notification.toString());
                if (notification.getSent().isBefore(completedDate)) {
                    sendMail = true;
                }

            }

            if (sendMail) {
                Optional<CivilServant> optionalLineManager = registryService.getCivilServantByUid(civilServant.getLineManagerUid());
                if (optionalLineManager.isPresent()) {
                    CivilServant lineManager = optionalLineManager.get();
                    notifyService.notifyOnComplete(civilServant.getLineManagerEmail(), "", govNotifyCompletedLearningTemplateId, civilServant.getFullName(), lineManager.getFullName(), course.getTitle());
                    notification = new Notification(course.getId(), identity.getUid(), COMPLETED);
                    notificationRepository.save(notification);
                } else {
                    LOGGER.error("User has line manager but line manager does not exist!");
                }
            }

        }

    }
    public void sendNotificationForCompletedLearning() throws NotificationClientException {
        Collection<Identity> identities = identityService.listAll();

        for (Identity identity : identities) {
            Boolean completed = false;

            LOGGER.debug("Got identity with uid {} ({})", identity.getUsername(), identities.size());
            try {
                CivilServant civilServant = registryService.getCivilServantByUid(identity.getUid());
                System.out.println(civilServant.getDepartmentCode());
                List<Course> courses = learningCatalogueService.getRequiredCoursesByDepartmentCode(civilServant.getDepartmentCode());
                LOGGER.debug("courses {}",courses.size());
                for (Course course : courses) {
                    Collection<CourseRecord> courseRecords = userRecordService.getUserRecord(identity.getUid(), String.format(COURSE_URI_FORMAT, course.getId()));
                    // okay we do not want to find a course without a completed record or without a record at all
                    if (courseRecords.size() != 0) {
                        for (CourseRecord courseRecord : courseRecords) {
                            LOGGER.debug("course is "+ courseRecord.getState());
                            if (courseRecord.getState() ==  State.COMPLETED) {
                                LOGGER.debug("course is COMPLETED");
                            }
                        }
                    }
                }
            } catch (HttpClientErrorException hce) {
                LOGGER.debug("Error getting details for {}",identity.getUid());
            }

            if (completed) {
                System.out.println(identity.getUid() + " COMPLETED!");

            } else {
                System.out.println(identity.getUid() + " NOT COMPLETED!");

                Boolean sendMail = false


                Optional<Notification> optionalNotification = notificationRepository.findFirstByIdentityUidAndNotificationType(identity.getUid(),COMPLETED);
                if (!optionalNotification.isPresent()) {
                    sendMail = true;
                } else {
                    if (notfication sent before completed date) {
                        sendMail = true;
                    }

                    }
                }

                if (sendMail) {
                    notifyService.notify("alan.work@teamsmog.com", "", govNotifyRequiredLearningDueTemplateId, "");
                    Notification notification = new Notification(identity.getUid());
                    notificationRepository.save(notification);
                }
            }
        }

    }

    @Transactional
    public void sendNotificationForIncompleteCourses() throws NotificationClientException {
>>>>>>> 7e5cce7... Utility
        Collection<Identity> identities = identityService.listAll();

        for (Identity identity : identities) {

            LOGGER.info("Got identity with uid {} ({})", identity.getUsername(), identities.size());
            try {
                Optional<CivilServant> optionalCivilServant = registryService.getCivilServantByUid(identity.getUid());

                if (optionalCivilServant.isPresent()) {
                    CivilServant civilServant = optionalCivilServant.get();
                    List<Course> courses = learningCatalogueService.getRequiredCoursesByDepartmentCode(civilServant.getDepartmentCode());
                    LOGGER.info("Got courses  ({})", courses.size());

                    for (Course course : courses) {
                        Collection<CourseRecord> courseRecords = userRecordService.getUserRecord(identity.getUid(), String.format(COURSE_URI_FORMAT, course.getId()));
                        // okay we do not want to find a course without a completed record or without a record at all

                        if (courseRecords.size() != 0) {
                            for (CourseRecord courseRecord : courseRecords) {
                                LOGGER.info("course completed  " + courseRecord.getCompletionDate());
                                if (courseRecord.getCompletionDate() != null) {
                                    LOGGER.info("course is COMPLETED");
                                    try {
                                        CheckAndNotifyLineManager(civilServant, identity, course, courseRecord.getCompletionDate());
                                    } catch (NotificationClientException nce) {
                                        LOGGER.error("Error notifying line manger about course completion");
                                        throw nce;
                                    }
                                }
                            }
                        }
                    }
                } else {
                    throw new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY);
                }
            } catch (HttpClientErrorException hce) {
                LOGGER.debug("Error getting details for {}",identity.getUid());
            }
        }
    }


    public void sendNotificationForIncompleteCourses() throws NotificationClientException {
        Collection<Identity> identities = identityService.listAll();

        for (Identity identity : identities) {
            LOGGER.debug("Got identity with uid {} and email {}", identity.getUid(), identity.getUsername());

            Optional<CivilServant> optionalCivilServant = registryService.getCivilServantByUid(identity.getUid());
            if (optionalCivilServant.isPresent()){
                CivilServant civilServant = optionalCivilServant.get();
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
                    LOGGER.debug("Next required by for course {} is {}", course, nextRequiredBy);

                    if (nextRequiredBy != null) {
                        checkAndAdd(course, identity, nextRequiredBy, now, incompleteCourses);
                    }
                }
                for (Map.Entry<Long, List<Course>> entry : incompleteCourses.entrySet()) {
                    sendNotificationForPeriod(identity, entry.getKey(), entry.getValue());
                }
            }
        }
    }

    void checkAndAdd(Course course, Identity identity, LocalDate nextRequiredBy, LocalDate now, Map<Long, List<Course>> incompleteCourses) {
        if(nextRequiredBy.isBefore(now)){
            return;
        }
        for (long notificationPeriod : NOTIFICATION_PERIODS) {
            LocalDate nowPlusNotificationPeriod = now.plusDays(notificationPeriod);
            if (nowPlusNotificationPeriod.isAfter(nextRequiredBy) || nowPlusNotificationPeriod.isEqual(nextRequiredBy) ) {
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
            Notification notification = new Notification(course.getId(), identity.getUid(), null);
            notificationRepository.save(notification);
        }
    }
}
