package uk.gov.cslearning.record.repository;

import org.joda.time.DateTime;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.record.domain.collections.States;

@Repository
public interface StatesRepository extends MongoRepository<States, String> {
    @Query(value = "{'agent.account.name' : ?0}", delete = true)
    void deleteAllByLearnerUid(String id);

    void deleteAllByAge(DateTime dateTime);
}
