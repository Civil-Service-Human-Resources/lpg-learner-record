package uk.gov.cslearning.record.service;

import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.dto.BookingDto;

import java.util.Optional;

public interface BookingService {
    @Transactional(readOnly = true)
    Optional<BookingDto> find(long bookingId);

    @Transactional
    BookingDto save(BookingDto booking);
}
