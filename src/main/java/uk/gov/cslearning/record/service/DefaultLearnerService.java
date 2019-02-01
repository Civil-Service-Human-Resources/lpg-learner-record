package uk.gov.cslearning.record.service;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.repository.LearnerRepository;

@Service
public class DefaultLearnerService implements LearnerService {

    private final LearnerRepository learnerRepository;

    private final BookingService bookingService;

    public DefaultLearnerService(LearnerRepository learnerRepository, BookingService bookingService) {
        this.learnerRepository = learnerRepository;
        this.bookingService = bookingService;
    }

    public void deleteLearnerByUid(String uid) {
        learnerRepository.findByUid(uid).ifPresent(learner -> {
            bookingService.deleteAllByLearner(learner);
            learnerRepository.delete(learner);
        });
    }
}
