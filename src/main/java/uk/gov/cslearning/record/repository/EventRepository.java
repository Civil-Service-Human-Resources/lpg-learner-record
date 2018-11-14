package uk.gov.cslearning.record.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.record.domain.Event;

import java.util.Optional;

@Repository
public interface EventRepository extends CrudRepository<Event, Integer> {
    Optional<Event> findByUid(String eventUid);
}
