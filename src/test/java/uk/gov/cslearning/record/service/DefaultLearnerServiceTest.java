package uk.gov.cslearning.record.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.record.domain.Learner;
import uk.gov.cslearning.record.repository.LearnerRepository;

import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultLearnerServiceTest {

    @Mock
    private LearnerRepository learnerRepository;

    @Mock
    private BookingService bookingService;

    @Mock
    private UserRecordService userRecordService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private InviteService inviteService;

    @InjectMocks
    private DefaultLearnerService defaultLearnerService;

    @Test
    public void shouldDeleteLearnerAndBookings() {
        String uid = "learner-test-uid";
        String learnerEmail = "test@example.com";

        Learner learner = new Learner();
        learner.setUid(uid);
        learner.setLearnerEmail(learnerEmail);

        when(learnerRepository.findByUid(uid)).thenReturn(Optional.of(learner));

        defaultLearnerService.deleteLearnerByUid(uid);

        verify(learnerRepository).findByUid(uid);
        verify(bookingService).deleteAllByLearner(learner);
        verify(learnerRepository).delete(learner);
        verify(userRecordService).deleteUserRecords(uid);
        verify(inviteService).deleteByLearnerEmail(learner.getLearnerEmail());
        verify(notificationService).deleteByLearnerUid(uid);
    }
}