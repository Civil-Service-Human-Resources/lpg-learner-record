package uk.gov.cslearning.record.service.scheduler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.cslearning.record.service.identity.IdentityService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LearningJobTest {

    @InjectMocks
    private LearningJob learningJob;

    @Mock
    private IdentityService identityService;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void doStuff() {
        learningJob.getIdentityListFromResponse();
    }
}