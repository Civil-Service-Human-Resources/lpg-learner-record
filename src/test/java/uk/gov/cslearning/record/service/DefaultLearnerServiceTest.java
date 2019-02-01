package uk.gov.cslearning.record.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.record.domain.Learner;
import uk.gov.cslearning.record.repository.LearnerRepository;

import java.util.Optional;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultLearnerServiceTest {

    @Mock
    private LearnerRepository learnerRepository;

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private DefaultLearnerService defaultLearnerService;

    @Test
    public void shouldDeleteLearnerAndBookings() {
        String uid = "learner-test-uid";
        Learner learner = new Learner();

        when(learnerRepository.findByUid(uid)).thenReturn(Optional.of(learner));

        defaultLearnerService.deleteLearnerByUid(uid);

        verify(learnerRepository).findByUid(uid);
        verify(bookingService).deleteAllByLearner(learner);
        verify(learnerRepository).delete(learner);
    }
}
