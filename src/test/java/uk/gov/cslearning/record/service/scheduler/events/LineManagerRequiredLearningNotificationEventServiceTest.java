package uk.gov.cslearning.record.service.scheduler.events;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.record.domain.scheduler.LineManagerRequiredLearningNotificationEvent;
import uk.gov.cslearning.record.repository.scheduler.LineManagerRequiredLearningNotificationEventRepository;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class LineManagerRequiredLearningNotificationEventServiceTest {

    @Mock
    private LineManagerRequiredLearningNotificationEventRepository lineManagerRequiredLearningNotificationEventRepository;

    @InjectMocks
    private LineManagerRequiredLearningNotificationEventService lineManagerRequiredLearningNotificationEventService;

    @Test
    public void shouldSave() {
        LineManagerRequiredLearningNotificationEvent lineManagerRequiredLearningNotificationEvent = new LineManagerRequiredLearningNotificationEvent();

        lineManagerRequiredLearningNotificationEventService.save(lineManagerRequiredLearningNotificationEvent);

        verify(lineManagerRequiredLearningNotificationEventRepository).save(lineManagerRequiredLearningNotificationEvent);
    }

    @Test
    public void shouldFindAll() {
        lineManagerRequiredLearningNotificationEventService.findAll();

        verify(lineManagerRequiredLearningNotificationEventRepository).findAll();
    }

    @Test
    public void shouldDelete() {
        LineManagerRequiredLearningNotificationEvent lineManagerRequiredLearningNotificationEvent = new LineManagerRequiredLearningNotificationEvent();

        lineManagerRequiredLearningNotificationEventService.delete(lineManagerRequiredLearningNotificationEvent);

        verify(lineManagerRequiredLearningNotificationEventRepository).delete(lineManagerRequiredLearningNotificationEvent);
    }
}
