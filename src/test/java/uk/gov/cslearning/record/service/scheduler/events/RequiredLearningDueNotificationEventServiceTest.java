package uk.gov.cslearning.record.service.scheduler.events;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.record.domain.scheduler.RequiredLearningDueNotificationEvent;
import uk.gov.cslearning.record.repository.scheduler.RequiredLearningDueNotificationEventRepository;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RequiredLearningDueNotificationEventServiceTest {

    @Mock
    private RequiredLearningDueNotificationEventRepository requiredLearningDueNotificationEventRepository;

    @InjectMocks
    private RequiredLearningDueNotificationEventService requiredLearningDueNotificationEventService;

    @Test
    public void shouldSave() {
        RequiredLearningDueNotificationEvent requiredLearningDueNotificationEvent = new RequiredLearningDueNotificationEvent();

        requiredLearningDueNotificationEventService.save(requiredLearningDueNotificationEvent);

        verify(requiredLearningDueNotificationEventRepository).save(requiredLearningDueNotificationEvent);
    }

    @Test
    public void shouldFindAll() {
        requiredLearningDueNotificationEventService.findAll();

        verify(requiredLearningDueNotificationEventRepository).findAll();
    }

    @Test
    public void shouldDelete() {
        RequiredLearningDueNotificationEvent requiredLearningDueNotificationEvent = new RequiredLearningDueNotificationEvent();

        requiredLearningDueNotificationEventService.delete(requiredLearningDueNotificationEvent);

        verify(requiredLearningDueNotificationEventRepository).delete(requiredLearningDueNotificationEvent);
    }
}
