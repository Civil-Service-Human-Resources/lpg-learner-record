package uk.gov.cslearning.record.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.record.domain.Statements;

import java.util.Optional;

@Repository
public interface StatementsRepository extends MongoRepository<Statements, String> {

    @Query("{ 'id' : ?0 }")
    Optional<Statements> findById(String id);

    @Query(value="{'person.display' : { $regex : '?0'} }", delete=true)
    void deleteAllByLearnerUid(String id);
}
