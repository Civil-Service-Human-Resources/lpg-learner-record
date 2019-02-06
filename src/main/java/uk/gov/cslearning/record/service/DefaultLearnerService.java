package uk.gov.cslearning.record.service;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.repository.LearnerRepository;

@Service
public class DefaultLearnerService implements LearnerService {

    private final LearnerRepository learnerRepository;

    private final BookingService bookingService;

    private final UserRecordService userRecordService;

    public DefaultLearnerService(LearnerRepository learnerRepository, BookingService bookingService, UserRecordService userRecordService) {
        this.learnerRepository = learnerRepository;
        this.bookingService = bookingService;
        this.userRecordService = userRecordService;
    }

    public void deleteLearnerByUid(String uid) {
        learnerRepository.findByUid(uid).ifPresent(learner -> {
            bookingService.deleteAllByLearner(learner);
            learnerRepository.delete(learner);
        });


        userRecordService.deleteUserRecords(uid);
    }
}
