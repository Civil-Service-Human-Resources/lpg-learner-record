package uk.gov.cslearning.record.service;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.dto.BookingDto;

import java.util.Optional;

@Service
public class DefaultBookingService implements BookingService {
    @Override
    public Optional<BookingDto> find(long bookingId) {
        throw new RuntimeException("Unimplemented");
    }

    @Override
    public BookingDto save(BookingDto booking) {
        throw new RuntimeException("Unimplemented");
    }
}
