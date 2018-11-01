package uk.gov.cslearning.record.service;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.dto.BookingDto;
import uk.gov.cslearning.record.repository.BookingRepository;

import java.util.Optional;

@Service
public class DefaultBookingService implements BookingService {
    private final BookingRepository bookingRepository;

    public DefaultBookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public Optional<BookingDto> find(long bookingId) {
        throw new RuntimeException("Unimplemented");
    }

    @Override
    public BookingDto save(BookingDto booking) {
        throw new RuntimeException("Unimplemented");
    }
}
