package uk.gov.cslearning.record.service;

import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.dto.BookingDto;
import uk.gov.cslearning.record.dto.BookingStatusDto;

import java.util.Optional;

public interface BookingService {
    @Transactional(readOnly = true)
    Optional<BookingDto> find(int bookingId);

    @Transactional
    BookingDto register(BookingDto booking);

    @Transactional
    BookingDto updateStatus(int bookingId, BookingStatusDto bookingStatus);

    @Transactional
    BookingDto unregister(BookingDto booking);
}
