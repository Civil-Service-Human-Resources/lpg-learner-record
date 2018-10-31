package uk.gov.cslearning.record.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.record.domain.Learner;

@Repository
public interface LearnerRepository extends CrudRepository<Learner, Long> {
}
