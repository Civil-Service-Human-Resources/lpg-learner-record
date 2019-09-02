package uk.gov.cslearning.record.service;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.domain.CompletedLearning;
import uk.gov.cslearning.record.repository.CompletedLearningRepository;

import java.util.List;

@Service
public class CompletedLearningService {

    private CompletedLearningRepository completedLearningRepository;

    public CompletedLearningService(CompletedLearningRepository completedLearningRepository) {
        this.completedLearningRepository = completedLearningRepository;
    }

    public CompletedLearning save(CompletedLearning completedLearning) {
        return completedLearningRepository.save(completedLearning);
    }

    public List<CompletedLearning> findAll() {
        return completedLearningRepository.findAll();
    }

    public void delete(CompletedLearning completedLearning) {
        completedLearningRepository.delete(completedLearning);
    }
}
