package uk.gov.cslearning.record.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.record.domain.Booking;
import uk.gov.cslearning.record.domain.Learner;
import uk.gov.cslearning.record.dto.BookingStatus;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer>, CustomBookingRepository {
    @Query("SELECT b FROM Booking b WHERE b.learner.learnerEmail = :email AND b.event.uid = :eventUid AND b.status IN :status")
    Optional<Booking> findByLearnerEmailAndEventUid(@Param("email") String learnerEmail, @Param("eventUid") String eventUid, @Param("status") List<BookingStatus> status);

    @Query("select b from Booking b where b.event.uid = :eventUid and b.learner.uid = :learnerUid AND b.status IN :status")
    Optional<Booking> findByEventUidLearnerUid(@Param("eventUid") String eventUid, @Param("learnerUid") String learnerUid, @Param("status") List<BookingStatus> status);

    List<Booking> findAllByBookingTimeBetween(Instant from, Instant to);

    @Query("select b from Booking b where b.learner.uid = :learnerUid")
    List<Booking> findAllByLearnerUid(@Param("learnerUid") String learnerUid);

    void deleteAllByLearner(Learner learner);

    void deleteAllByBookingTimeBefore(Instant instant);

    List<Booking> findAllByBookingTimeBefore(Instant instant);
}
