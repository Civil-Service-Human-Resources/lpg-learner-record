package uk.gov.cslearning.record.service.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.State;
import uk.gov.cslearning.record.service.*;
import uk.gov.cslearning.record.service.catalogue.Course;
import uk.gov.cslearning.record.service.catalogue.LearningCatalogueService;
import uk.gov.cslearning.record.service.identity.Identity;
import uk.gov.cslearning.record.service.identity.IdentityService;
import uk.gov.service.notify.NotificationClientException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class LearningJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(LearningJob.class);

    private static final String COURSE_URI_FORMAT = "http://cslearning.gov.uk/courses/%s";

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
            for (Course c: courses) {
                Collection<CourseRecord> courseRecords = userRecordService.getUserRecord(identity.getUid(), String.format(COURSE_URI_FORMAT, c.getId()));

                boolean completed = false;
                for (CourseRecord courseRecord: courseRecords) {
                    if (courseRecord.getState() == State.COMPLETED){
                        completed = true;
                        break;
                    }
                    // get next required by
                    // if next req, is less than a month and havent sent week send week etc
                }
                if (!completed){
                    incompleteCourses.add(c);
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
