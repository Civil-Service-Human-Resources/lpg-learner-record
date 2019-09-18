package uk.gov.cslearning.record.service.scheduler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.record.service.LearnerService;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SchedulerTest {

    @Mock
    private SchedulerService schedulerService;

    @Mock
    private SchedulerEventNotificationService schedulerEventNotificationService;

    @Mock
    private LearnerService learnerService;

    @InjectMocks
    private Scheduler scheduler;

    @Test
    public void shouldProcessReminderNotificationForIncompleteLearning() {
        scheduler.processReminderNotificationForIncompleteLearning();

        verify(schedulerService).processReminderNotificationForIncompleteLearning();
    }

    @Test
    public void deleteOldStatements() {
        scheduler.deleteOldStatements();

        verify(learnerService).deleteOldStatements();
    }

    @Test
    public void shouldSendReminderNotificationForIncompleteLearning() {
        scheduler.sendReminderNotificationForIncompleteLearning();

        verify(schedulerEventNotificationService).sendReminderNotificationForIncompleteLearning();
    }
}
