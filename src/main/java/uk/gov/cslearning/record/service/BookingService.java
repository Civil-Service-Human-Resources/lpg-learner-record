package uk.gov.cslearning.record.service;

import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.domain.Booking;
import uk.gov.cslearning.record.dto.BookingDto;
import uk.gov.cslearning.record.dto.BookingStatusDto;

import java.util.Optional;

public interface BookingService {
    @Transactional(readOnly = true)
    Optional<BookingDto> find(int bookingId);

    @Transactional(readOnly = true)
    Optional<BookingDto> find(String eventUid, String learnerUid);

    @Transactional(readOnly = true)
    Iterable<BookingDto> listByEventUid(String eventUid);

    @Transactional
    BookingDto register(BookingDto booking);

    @Transactional
    BookingDto updateStatus(int bookingId, BookingStatusDto bookingStatus);

    @Transactional
    BookingDto updateStatus(String eventUid, String learnerUid, BookingStatusDto bookingStatus);

    @Transactional
    BookingDto unregister(BookingDto booking);

    @Transactional
    BookingDto unregister(Booking booking);

    @Transactional(readOnly = true)
    Optional<Booking> findActiveBookingByEmailAndEvent(String learnerEmail, String eventUid);
}
