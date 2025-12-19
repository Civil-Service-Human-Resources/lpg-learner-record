package uk.gov.cslearning.record.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.record.domain.Booking;
import uk.gov.cslearning.record.domain.BookingStatus;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    Optional<Booking> findByEventUidAndLearnerUidAndStatusIn(@Param("eventUid") String eventUid, @Param("learnerUid") String learnerUid, @Param("status") List<BookingStatus> status);

    Optional<Booking> findByEventUidAndLearnerUid(@Param("eventUid") String eventUid, @Param("learnerUid") String learnerUid);

    List<Booking> findAllByBookingTimeBetween(Instant from, Instant to);

    void deleteAllByLearnerUid(String learnerUid);

    void deleteAllByBookingTimeBefore(Instant instant);
}
