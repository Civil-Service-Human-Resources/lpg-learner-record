package uk.gov.cslearning.record.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.record.domain.Booking;
import uk.gov.cslearning.record.domain.Learner;

import java.util.Optional;

@Repository
public interface BookingRepository extends CrudRepository<Booking, Integer>, CustomBookingRepository {
    @Query("SELECT l FROM Booking b INNER JOIN Learner l ON b.learner = l INNER JOIN Event e ON b.event = e WHERE l.learnerEmail = ?1 AND e.uid = ?2")
    Optional<Learner> findByLearnerEmailAndEventUid(String learnerEmail, String eventUid);
}
