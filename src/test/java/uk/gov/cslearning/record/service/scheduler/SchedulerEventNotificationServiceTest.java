package uk.gov.cslearning.record.service.scheduler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.record.domain.scheduler.RequiredLearningDueNotificationEvent;
import uk.gov.cslearning.record.service.scheduler.events.RequiredLearningDueNotificationEventService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SchedulerEventNotificationServiceTest {

    @Mock
    private RequiredLearningDueNotificationEventService requiredLearningDueNotificationEventService;

    @Mock
    private ScheduledNotificationsService scheduledNotificationsService;

    @InjectMocks
    private SchedulerEventNotificationService schedulerEventNotificationService;

    @Test
    public void shouldSendReminderNotificationForIncompleteLearning() {
        RequiredLearningDueNotificationEvent requiredLearningDueNotificationEvent1 = new RequiredLearningDueNotificationEvent();
        requiredLearningDueNotificationEvent1.setId(1L);
        RequiredLearningDueNotificationEvent requiredLearningDueNotificationEvent2 = new RequiredLearningDueNotificationEvent();
        requiredLearningDueNotificationEvent2.setId(2L);
        List<RequiredLearningDueNotificationEvent> requiredLearningDueNotificationEventList = Arrays.asList(requiredLearningDueNotificationEvent1, requiredLearningDueNotificationEvent2);

        when(requiredLearningDueNotificationEventService.findAll()).thenReturn(requiredLearningDueNotificationEventList);
        when(scheduledNotificationsService.hasRequiredLearningDueNotificationBeenSent(requiredLearningDueNotificationEvent1)).thenReturn(true);
        when(scheduledNotificationsService.hasRequiredLearningDueNotificationBeenSent(requiredLearningDueNotificationEvent2)).thenReturn(false);

        schedulerEventNotificationService.sendReminderNotificationForIncompleteLearning();

        verify(scheduledNotificationsService).sendRequiredLearningDueNotification(requiredLearningDueNotificationEvent2);
        verify(scheduledNotificationsService, never()).sendRequiredLearningDueNotification(requiredLearningDueNotificationEvent1);
    }
}
