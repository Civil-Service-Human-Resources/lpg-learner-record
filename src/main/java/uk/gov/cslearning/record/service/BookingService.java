package uk.gov.cslearning.record.service;

import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.domain.Booking;
import uk.gov.cslearning.record.dto.BookingDto;
import uk.gov.cslearning.record.dto.BookingStatusDto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

public interface BookingService {
    @Transactional(readOnly = true)
    Optional<BookingDto> find(int bookingId);

    @Transactional(readOnly = true)
    Optional<BookingDto> find(String eventUid, String learnerUid);

    @Transactional(readOnly = true)
    Iterable<BookingDto> listByEventUid(String eventUid);

    @Transactional(readOnly = true)
    Optional<BookingDto> findByLearnerUidAndEventUid(String eventUid, String learnerUid);

    @Transactional
    BookingDto updateStatus(int bookingId, BookingStatusDto bookingStatus);

    @Transactional
    BookingDto updateStatus(String eventUid, String learnerUid, BookingStatusDto bookingStatus);

    BookingDto create(String eventUid, BookingDto bookingDto);

    BookingDto update(Booking booking, BookingStatusDto bookingStatusDto);
    
    @Transactional(readOnly = true)
    Iterable<BookingDto> findAllForPeriod(LocalDate from, LocalDate to);

    void deleteAllByLearnerUid(String learnerUid);

    @Transactional
    void deleteAllByAge(Instant instant);
}
