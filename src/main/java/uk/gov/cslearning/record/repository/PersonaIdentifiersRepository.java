package uk.gov.cslearning.record.repository;

import org.joda.time.DateTime;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.record.domain.collections.Personaidentifiers;

import java.util.Optional;

@Repository
public interface PersonaIdentifiersRepository extends MongoRepository<Personaidentifiers, String> {
    @Query(value = "{'uniqueIdentifier.value.name' : ?0}", delete = true)
    void deleteByLearnerUid(String id);

    @Query(value = "{'uniqueIdentifier.value.name' : ?0}")
    Optional<Personaidentifiers> findByLearnerUid(String id);

    void deleteAllByAge(DateTime dateTime);
}
