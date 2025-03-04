package uk.gov.cslearning.record.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.record.domain.Booking;
import uk.gov.cslearning.record.domain.BookingStatus;
import uk.gov.cslearning.record.domain.Learner;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    @Query("SELECT b FROM Booking b WHERE b.learner.learnerEmail = :email AND b.event.uid = :eventUid AND b.status IN :status")
    Optional<Booking> findByLearnerEmailAndEventUid(@Param("email") String learnerEmail, @Param("eventUid") String eventUid, @Param("status") List<BookingStatus> status);

    Optional<Booking> findByEventUidAndLearnerUidAndStatusIn(@Param("eventUid") String eventUid, @Param("learnerUid") String learnerUid, @Param("status") List<BookingStatus> status);

    Optional<Booking> findByEventUidAndLearnerUid(@Param("eventUid") String eventUid, @Param("learnerUid") String learnerUid);

    List<Booking> findAllByBookingTimeBetween(Instant from, Instant to);

    void deleteAllByLearner(Learner learner);

    void deleteAllByBookingTimeBefore(Instant instant);
}
