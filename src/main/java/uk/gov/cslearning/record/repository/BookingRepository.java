package uk.gov.cslearning.record.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.record.domain.Booking;

import java.util.Collection;

@Repository
public interface BookingRepository extends CrudRepository<Booking, Integer> {

    @Query("SELECT b FROM Booking b INNER JOIN Event e ON b.event = e WHERE e.eventUid = ?1")
    Collection<Booking> listByEventUid(String eventUid);
}
