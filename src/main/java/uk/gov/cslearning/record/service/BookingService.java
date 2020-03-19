package uk.gov.cslearning.record.service;

import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.domain.Booking;
import uk.gov.cslearning.record.domain.Learner;
import uk.gov.cslearning.record.dto.BookingDto;
import uk.gov.cslearning.record.dto.BookingStatusDto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingService {
    @Transactional(readOnly = true)
    Optional<BookingDto> find(int bookingId);

    @Transactional(readOnly = true)
    Optional<BookingDto> find(String eventUid, String learnerUid);

    @Transactional(readOnly = true)
    Iterable<BookingDto> listByEventUid(String eventUid);

    @Transactional(readOnly = true)
    Iterable<BookingDto> listByLearnerUid(String learnerUid);

    @Transactional(readOnly = true)
    Optional<BookingDto> findByLearnerUidAndEventUid(String eventUid, String learnerUid);

    @Transactional
    BookingDto register(BookingDto booking);

    @Transactional
    BookingDto updateStatus(int bookingId, BookingStatusDto bookingStatus);

    @Transactional
    void foo();

    @Transactional
    BookingDto updateStatus(String eventUid, String learnerUid, BookingStatusDto bookingStatus);

    @Transactional
    BookingDto unregister(BookingDto booking);

    @Transactional
    BookingDto unregister(Booking booking, String cancellationReason);

    @Transactional(readOnly = true)
    Optional<Booking> findActiveBookingByEmailAndEvent(String learnerEmail, String eventUid);

    @Transactional(readOnly = true)
    List<BookingDto> findAll();

    @Transactional(readOnly = true)
    List<BookingDto> findAllForPeriod(LocalDate from, LocalDate to);

    @Transactional
    void deleteAllByLearner(Learner learner);

    @Transactional
    void deleteAllByAge(Instant instant);
}
