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
    @Query("SELECT b FROM Booking b WHERE b.learner.learnerEmail = :email AND b.event.uid = :eventUid AND b.status IN :status")
    Optional<Booking> findByLearnerEmailAndEventUid(@Param("email") String learnerEmail, @Param("eventUid") String eventUid, @Param("status") List<String> status);

    @Query("select b from Booking b where b.event.uid = :eventUid and b.learner.uid = :learnerUid")
    Optional<Booking> findByEventUidLearnerUid(@Param("eventUid") String eventUid, @Param("learnerUid") String learnerUid);
}
