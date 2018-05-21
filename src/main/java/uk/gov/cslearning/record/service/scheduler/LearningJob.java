package uk.gov.cslearning.record.service.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.State;
import uk.gov.cslearning.record.service.*;
import uk.gov.cslearning.record.service.identity.Identity;
import uk.gov.cslearning.record.service.identity.IdentityService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LearningJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(LearningJob.class);

    private static final String COURSE_URI_FORMAT = "http://cslearning.gov.uk/courses/%s";

    private IdentityService identityService;

    @Autowired
    private UserRecordService userRecordService;

    @Autowired
    private RegistryService registryService;

    @Autowired
    private LearningCatalogueService learningCatalogueService;

    public LearningJob(IdentityService identityService) {
        this.identityService = identityService;
    }

    public void sendNotificationForIncompleteCourses() {
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
                }
                if (!completed){
                    incompleteCourses.add(c);
                }
            }
            if (!incompleteCourses.isEmpty()) {
                // format and send
            }
        }
    }
}
