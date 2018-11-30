package uk.gov.cslearning.record.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.record.domain.Booking;

import java.util.Optional;

@Repository
public interface BookingRepository extends CrudRepository<Booking, Integer>, CustomBookingRepository {

    @Query("select b from Booking b where b.event.uid = :eventUid and b.learner.uid = :learnerUid")
    Optional<Booking> findByEventUidLearnerUid(@Param("eventUid") String eventUid, @Param("learnerUid") String learnerUid);
}
