package uk.gov.cslearning.record.service;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.domain.factory.BookingFactory;
import uk.gov.cslearning.record.dto.BookingDto;
import uk.gov.cslearning.record.dto.BookingStatus;
import uk.gov.cslearning.record.dto.factory.BookingDtoFactory;
import uk.gov.cslearning.record.repository.BookingRepository;
import uk.gov.cslearning.record.service.xapi.XApiService;

import java.io.IOException;
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
    public Optional<BookingDto> find(long bookingId) {

        BookingDto bookingDto = bookingRepository.findById(bookingId).map(
                bookingDtoFactory::create
        ).orElse(null);

        return Optional.ofNullable(bookingDto);
    }

    @Override
    public BookingDto save(BookingDto bookingDto) {
        if (bookingDto.getStatus().equals(BookingStatus.APPROVED)) {
            xApiService.register(bookingDto);
        }

        return bookingDtoFactory.create(bookingRepository.save(bookingFactory.create(bookingDto)));
    }
}