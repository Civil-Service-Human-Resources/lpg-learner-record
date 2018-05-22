package uk.gov.cslearning.record.service.scheduler;

import jdk.vm.ci.meta.Local;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.Notification;
import uk.gov.cslearning.record.domain.State;
import uk.gov.cslearning.record.service.*;
import uk.gov.cslearning.record.service.catalogue.Course;
import uk.gov.cslearning.record.service.catalogue.LearningCatalogueService;
import uk.gov.cslearning.record.service.identity.Identity;
import uk.gov.cslearning.record.service.identity.IdentityService;
import uk.gov.service.notify.NotificationClientException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class LearningJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(LearningJob.class);

    private static final String COURSE_URI_FORMAT = "http://cslearning.gov.uk/courses/%s";

    private static final long[] NOTIFICATION_PERIODS = new long[] { 30, 7, 1 };

    @Value("${govNotify.template.requiredLearningDue}")
    private String govNotifyRequiredLearningDueTemplateId;

    private IdentityService identityService;

    private RegistryService registryService;

    private LearningCatalogueService learningCatalogueService;

    private NotifyService notifyService;

    @Autowired
    private UserRecordService userRecordService;

    @Autowired
    public LearningJob(IdentityService identityService, RegistryService registryService, LearningCatalogueService learningCatalogueService, NotifyService notifyService) {
        this.identityService = identityService;
        this.registryService = registryService;
        this.learningCatalogueService = learningCatalogueService;
        this.notifyService = notifyService;
    }

    public void sendNotificationForIncompleteCourses() throws NotificationClientException{
        Collection<Identity> identities = identityService.listAll();

        for (Identity identity: identities){
            LOGGER.info("Got identity with uid {} and username {}", identity.getUid(), identity.getUsername());
            CivilServant civilServant = registryService.getCivilServantByUid(identity.getUid());

            List<Course> courses = learningCatalogueService.getRequiredCoursesByDepartmentCode(civilServant.getDepartmentCode());
            List<Course> incompleteCourses = new ArrayList<>();
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

                if (nextRequiredBy != null) {
                    Notification notification = null; // TODO: load most recent notification for this user / course

                    for (long days : NOTIFICATION_PERIODS) {
                        // TODO: check against last notification send date
                        if (now.plusDays(days).isAfter(nextRequiredBy)) {
                            incompleteCourses.add(c);
                            break;
                        }
                    }
                }
            }
            if (!incompleteCourses.isEmpty()) {
                StringBuilder requiredLearning = new StringBuilder();
                for (Course c: incompleteCourses){
                    requiredLearning.append(c.getTitle() + "\n");
                }
                notifyService.notify(identity.getUsername(), requiredLearning.toString(), govNotifyRequiredLearningDueTemplateId);
            }
        }
    }
}
