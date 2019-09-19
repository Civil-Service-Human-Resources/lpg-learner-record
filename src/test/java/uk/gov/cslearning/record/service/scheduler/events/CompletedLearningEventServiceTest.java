package uk.gov.cslearning.record.service.scheduler.events;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.record.domain.scheduler.CompletedLearningEvent;
import uk.gov.cslearning.record.repository.CompletedLearningEventRepository;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CompletedLearningEventServiceTest {

    @Mock
    private CompletedLearningEventRepository completedLearningEventRepository;

    @InjectMocks
    private CompletedLearningEventService completedLearningEventService;

    @Test
    public void shouldSave() {
        CompletedLearningEvent completedLearningEvent = new CompletedLearningEvent();

        completedLearningEventService.save(completedLearningEvent);

        verify(completedLearningEventRepository).save(completedLearningEvent);
    }

    @Test
    public void shouldFindAll() {
        completedLearningEventService.findAll();

        verify(completedLearningEventRepository).findAll();
    }

    @Test
    public void shouldDelete() {
        CompletedLearningEvent completedLearningEvent = new CompletedLearningEvent();

        completedLearningEventService.delete(completedLearningEvent);

        verify(completedLearningEventRepository).delete(completedLearningEvent);
    }
}
