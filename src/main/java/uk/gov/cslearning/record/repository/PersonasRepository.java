package uk.gov.cslearning.record.repository;

import org.joda.time.DateTime;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.record.domain.collections.Personas;

import java.util.Optional;

@Repository
public interface PersonasRepository extends MongoRepository<Personas, String> {
    Optional<Personas> findByUid(String uid);

    void deleteAllByAge(DateTime dateTime);
}
