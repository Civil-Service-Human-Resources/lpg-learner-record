package uk.gov.cslearning.record.service.scheduler.events;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.domain.scheduler.CompletedLearningEvent;
import uk.gov.cslearning.record.repository.CompletedLearningEventRepository;

import java.util.List;

@Service
public class CompletedLearningEventService {
    private CompletedLearningEventRepository completedLearningEventRepository;

    public CompletedLearningEventService(CompletedLearningEventRepository completedLearningEventRepository) {
        this.completedLearningEventRepository = completedLearningEventRepository;
    }

    public CompletedLearningEvent save(CompletedLearningEvent completedLearningEvent) {
        return completedLearningEventRepository.save(completedLearningEvent);
    }

    public List<CompletedLearningEvent> findAll() {
        return completedLearningEventRepository.findAll();
    }

    public void delete(CompletedLearningEvent completedLearning) {
        completedLearningEventRepository.delete(completedLearning);
    }
}
