package uk.gov.cslearning.record.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.record.domain.Booking;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends CrudRepository<Booking, Integer>, CustomBookingRepository {
    @Query("SELECT b FROM Booking b WHERE b.learner.learnerEmail = :email AND b.event.uid = :eventUid AND b.status in :status")
    Optional<Booking> findByLearnerEmailAndEventUid(@Param("email") String learnerEmail, @Param("eventUid") String eventUid, @Param("status") List<String> status);
}
