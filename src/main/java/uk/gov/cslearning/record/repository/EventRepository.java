package uk.gov.cslearning.record.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.record.domain.Event;

@Repository
public interface EventRepository extends CrudRepository<Event, Integer> {

    @Query("SELECT e FROM Event e WHERE e.eventUid = ?1")
    Iterable<Event> findByCatalogueId(String catalogueId);
}
