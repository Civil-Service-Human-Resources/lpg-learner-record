package uk.gov.cslearning.record.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.dto.BookingStatus;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends CrudRepository<Event, Integer> {
    Optional<Event> findByUid(String eventUid);

    Integer countByBookings_StatusInAndIdEquals(Collection<BookingStatus> statuses, Integer id);

    List<Event> findByUidIn(List<String> uids);



}
