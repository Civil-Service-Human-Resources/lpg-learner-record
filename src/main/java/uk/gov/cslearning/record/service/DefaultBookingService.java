package uk.gov.cslearning.record.service;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.domain.Learner;
import uk.gov.cslearning.record.domain.factory.BookingFactory;
import uk.gov.cslearning.record.dto.BookingDto;
import uk.gov.cslearning.record.dto.BookingStatus;
import uk.gov.cslearning.record.dto.BookingStatusDto;
import uk.gov.cslearning.record.dto.factory.BookingDtoFactory;
import uk.gov.cslearning.record.exception.BookingNotFoundException;
import uk.gov.cslearning.record.repository.BookingRepository;
import uk.gov.cslearning.record.repository.EventRepository;
import uk.gov.cslearning.record.repository.LearnerRepository;
import uk.gov.cslearning.record.service.xapi.XApiService;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DefaultBookingService implements BookingService {

    private final BookingFactory bookingFactory;
    private final BookingDtoFactory bookingDtoFactory;
    private final BookingRepository bookingRepository;
    private final LearnerRepository learnerRepository;
    private final EventRepository eventRepository;
    private final XApiService xApiService;

    public DefaultBookingService(BookingFactory bookingFactory, BookingDtoFactory bookingDtoFactory, BookingRepository bookingRepository, LearnerRepository learnerRepository, EventRepository eventRepository, XApiService xApiService) {
        this.bookingFactory = bookingFactory;
        this.bookingDtoFactory = bookingDtoFactory;
        this.bookingRepository = bookingRepository;
        this.learnerRepository = learnerRepository;
        this.eventRepository = eventRepository;
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
    public Iterable<BookingDto> listByEventUid(String eventUid){
        Optional<Event> event = eventRepository.findByEventUid(eventUid);

        if(event.isPresent()) {
            Iterable<BookingDto> bookings = event.get().getBookings().stream().map(
                    bookingDtoFactory::create
            ).collect(Collectors.toList());

            return bookings;
        }
        return new ArrayList<>();
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

        if (booking.getStatus().equals(BookingStatus.CONFIRMED)) {
            throw new IllegalStateException("Cannot update a confirmed booking");
        }

        booking.setStatus(bookingStatus.getStatus());

        return bookingStatus.getStatus().equals(BookingStatus.CONFIRMED) ? register(booking) : unregister(booking);
    }

    @Override
    public BookingDto unregister(BookingDto bookingDto) {
        if (bookingDto.getStatus().equals(BookingStatus.CANCELLED)) {
            xApiService.unregister(bookingDto);
        }

        return save(bookingDto);
    }

    private BookingDto save(BookingDto bookingDto) {
        Optional<Learner> learner = learnerRepository.findByByUid(bookingDto.getLearner());
        return bookingDtoFactory.create(bookingRepository.save(bookingFactory.create(bookingDto, learner)));
    }
}
