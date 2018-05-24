package uk.gov.cslearning.record.service.scheduler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.cslearning.record.service.NotifyService;
import uk.gov.cslearning.record.service.RegistryService;
import uk.gov.cslearning.record.service.UserRecordService;
import uk.gov.cslearning.record.service.catalogue.LearningCatalogueService;
import uk.gov.cslearning.record.service.identity.IdentityService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LearningJobTest {

    @Autowired
    private LearningJob learningJob;


    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void sendNotificationForIncompleteCourses() throws Exception {
        learningJob.sendNotificationForIncompleteCourses();
    }
}