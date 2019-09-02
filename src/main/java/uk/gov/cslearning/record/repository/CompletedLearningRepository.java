package uk.gov.cslearning.record.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.record.domain.CompletedLearning;

@Repository
public interface CompletedLearningRepository extends JpaRepository<CompletedLearning, Long> {
}
