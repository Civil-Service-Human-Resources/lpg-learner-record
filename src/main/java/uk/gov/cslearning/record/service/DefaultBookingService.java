package uk.gov.cslearning.record.service;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.domain.Booking;
import uk.gov.cslearning.record.domain.factory.BookingFactory;
import uk.gov.cslearning.record.dto.BookingDto;
import uk.gov.cslearning.record.dto.BookingStatus;
import uk.gov.cslearning.record.dto.BookingStatusDto;
import uk.gov.cslearning.record.dto.factory.BookingDtoFactory;
import uk.gov.cslearning.record.exception.BookingNotFoundException;
import uk.gov.cslearning.record.repository.BookingRepository;
import uk.gov.cslearning.record.service.xapi.XApiService;

import java.util.Optional;

@Service
public class DefaultBookingService implements BookingService {

    private final BookingFactory bookingFactory;
    private final BookingDtoFactory bookingDtoFactory;
    private final BookingRepository bookingRepository;
    private final XApiService xApiService;

    public DefaultBookingService(BookingFactory bookingFactory, BookingDtoFactory bookingDtoFactory, BookingRepository bookingRepository, XApiService xApiService) {
        this.bookingFactory = bookingFactory;
        this.bookingDtoFactory = bookingDtoFactory;
        this.bookingRepository = bookingRepository;
        this.xApiService = xApiService;
    }

    @Override
    public Optional<BookingDto> find(int bookingId) {

        BookingDto bookingDto = bookingRepository.findById(bookingId).map(
                bookingDtoFactory::create
        ).orElse(null);

        return Optional.ofNullable(bookingDto);
    }

    @Override
    public BookingDto register(BookingDto bookingDto) {
        if (bookingDto.getStatus().equals(BookingStatus.CONFIRMED)) {
            xApiService.register(bookingDto);
        }

        return save(bookingDto);
    }

    @Override
    public BookingDto updateStatus(int bookingId, BookingStatusDto bookingStatus) {
        BookingDto booking = find(bookingId).orElseThrow(() -> new BookingNotFoundException(bookingId));

        booking.setStatus(bookingStatus.getStatus());

        return bookingStatus.getStatus().equals(BookingStatus.CONFIRMED) ? register(booking) : unregister(booking);
    }

    @Override
    public BookingDto unregister(BookingDto bookingDto) {
        if (bookingDto.getStatus().equals(BookingStatus.CONFIRMED)) {
            xApiService.unregister(bookingDto);
        }

        bookingDto.setStatus(BookingStatus.CANCELLED);
        return save(bookingDto);
    }

    @Override
    public BookingDto unregister(Booking booking) {
        return unregister(bookingDtoFactory.create(booking));
    }


    private BookingDto save(BookingDto bookingDto) {
        return bookingDtoFactory.create(bookingRepository.saveBooking(bookingFactory.create(bookingDto)));
    }
}
