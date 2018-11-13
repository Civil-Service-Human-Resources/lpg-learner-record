package uk.gov.cslearning.record.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.record.domain.Learner;

import java.util.Optional;

@Repository
public interface LearnerRepository extends CrudRepository<Learner, Integer> {

    @Query("SELECT l FROM Learner l WHERE l.uid = ?1")
    Optional<Learner> getLearnerByUid(String uid);
}
