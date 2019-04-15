package uk.gov.cslearning.record.service;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.repository.LearnerRepository;

@Service
public class DefaultLearnerService implements LearnerService {

    private final LearnerRepository learnerRepository;

    private final BookingService bookingService;

    private final UserRecordService userRecordService;

    private final InviteService inviteService;

    private final NotificationService notificationService;


    public DefaultLearnerService(LearnerRepository learnerRepository, BookingService bookingService, UserRecordService userRecordService, InviteService inviteService, NotificationService notificationService) {
        this.learnerRepository = learnerRepository;
        this.bookingService = bookingService;
        this.userRecordService = userRecordService;
        this.inviteService = inviteService;
        this.notificationService = notificationService;
    }

    public void deleteLearnerByUid(String uid) {
        learnerRepository.findByUid(uid).ifPresent(learner -> {
            bookingService.deleteAllByLearner(learner);
            inviteService.deleteByLearnerEmail(learner.getLearnerEmail());
            notificationService.deleteByLearnerUid(learner.getUid());
            learnerRepository.delete(learner);
        });

        userRecordService.deleteUserRecords(uid);
    }
}
