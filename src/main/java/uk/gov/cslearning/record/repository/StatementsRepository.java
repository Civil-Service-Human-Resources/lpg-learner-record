package uk.gov.cslearning.record.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.record.domain.Statements;

@Repository
public interface StatementsRepository extends MongoRepository<Statements, String> {
    @Query(value="{'person.display' : { $regex : '?0'} }", delete=true)
    void deleteAllByLearnerUid(String id);
}
