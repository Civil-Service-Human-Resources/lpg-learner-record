package uk.gov.cslearning.record.service;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.domain.Booking;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.domain.factory.BookingFactory;
import uk.gov.cslearning.record.dto.BookingCancellationReason;
import uk.gov.cslearning.record.dto.BookingDto;
import uk.gov.cslearning.record.dto.BookingStatus;
import uk.gov.cslearning.record.dto.BookingStatusDto;
import uk.gov.cslearning.record.dto.factory.BookingDtoFactory;
import uk.gov.cslearning.record.exception.BookingNotFoundException;
import uk.gov.cslearning.record.notifications.service.NotificationService;
import uk.gov.cslearning.record.repository.BookingRepository;
import uk.gov.cslearning.record.repository.EventRepository;
import uk.gov.cslearning.record.service.xapi.XApiService;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DefaultBookingService implements BookingService {

    private final BookingFactory bookingFactory;
    private final BookingDtoFactory bookingDtoFactory;
    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final XApiService xApiService;
    private final NotificationService notificationService;
    private final MessageService messageService;

    public DefaultBookingService(BookingFactory bookingFactory, BookingDtoFactory bookingDtoFactory, BookingRepository bookingRepository, EventRepository eventRepository, XApiService xApiService, NotificationService notificationService, MessageService messageService) {
        this.bookingFactory = bookingFactory;
        this.bookingDtoFactory = bookingDtoFactory;
        this.bookingRepository = bookingRepository;
        this.eventRepository = eventRepository;
        this.xApiService = xApiService;
        this.notificationService = notificationService;
        this.messageService = messageService;
    }

    @Override
    public Optional<BookingDto> find(int bookingId) {

        BookingDto bookingDto = bookingRepository.findById(bookingId).map(
                bookingDtoFactory::create
        ).orElse(null);

        return Optional.ofNullable(bookingDto);
    }

    @Override
    public Optional<BookingDto> find(String eventUid, String learnerUid) {
        List<BookingStatus> status = Arrays.asList(BookingStatus.REQUESTED, BookingStatus.CONFIRMED, BookingStatus.CANCELLED);

        BookingDto bookingDto = bookingRepository.findByEventUidLearnerUid(eventUid, learnerUid, status).map(
                bookingDtoFactory::create
        ).orElse(null);

        return Optional.ofNullable(bookingDto);
    }

    @Override
    public Iterable<BookingDto> listByEventUid(String eventUid) {
        Optional<Event> event = eventRepository.findByUid(eventUid);

        if (event.isPresent()) {
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
            notificationService.send(messageService.createBookedMessage(bookingDto));
        } else if (bookingDto.getStatus().equals(BookingStatus.REQUESTED)) {
            notificationService.send(messageService.createRegisteredMessage(bookingDto));
        }
        return save(bookingDto);
    }

    @Override
    public BookingDto updateStatus(int bookingId, BookingStatusDto bookingStatus) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException(bookingId));

        return updateStatus(booking, bookingStatus);
    }

    @Override
    public BookingDto updateStatus(String eventUid, String bookingUid, BookingStatusDto bookingStatus) {
        List<BookingStatus> status = Arrays.asList(BookingStatus.REQUESTED, BookingStatus.CONFIRMED);

        Booking booking = bookingRepository.findByEventUidLearnerUid(eventUid, bookingUid, status).orElseThrow(() -> new BookingNotFoundException(eventUid, bookingUid));
        return updateStatus(booking, bookingStatus);
    }

    private BookingDto updateStatus(Booking booking, BookingStatusDto bookingStatusDto) {
        BookingDto bookingDto = bookingDtoFactory.create(booking);


        if (bookingStatusDto.getStatus().equals(BookingStatus.CONFIRMED)) {
            bookingDto.setStatus(bookingStatusDto.getStatus());
            bookingDto.setConfirmationTime(Instant.now());
            return register(bookingDto);
        } else {
            bookingDto.setCancellationReason(BookingCancellationReason.valueOf(bookingStatusDto.getCancellationReason()));
            notificationService.send(messageService.createUnregisterMessage(bookingDto, bookingDto.getCancellationReason().getValue()));
            return unregister(bookingDto);
        }
    }

    @Override
    public BookingDto unregister(BookingDto bookingDto) {
        if (bookingDto.getStatus().equals(BookingStatus.CONFIRMED)) {
            xApiService.unregister(bookingDto);
        }

        bookingDto.setStatus(BookingStatus.CANCELLED);
        bookingDto.setCancellationTime(Instant.now());

        return save(bookingDto);
    }

    @Override
    public BookingDto unregister(Booking booking, String cancellationReason) {
        notificationService.send(messageService.createCancelEventMessage(booking, cancellationReason));
        return unregister(bookingDtoFactory.create(booking));
    }

    @Override
    public Optional<Booking> findActiveBookingByEmailAndEvent(String learnerEmail, String eventUid) {
        List<BookingStatus> status = Arrays.asList(BookingStatus.REQUESTED, BookingStatus.CONFIRMED);

        return bookingRepository.findByLearnerEmailAndEventUid(learnerEmail, eventUid, status);
    }

    @Override
    public List<BookingDto> findAll() {
        return bookingRepository.findAll().stream()
                .map(bookingDtoFactory::create)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> findAllForPeriod(LocalDate from, LocalDate to) {
        Instant periodStart = ZonedDateTime.of(from.atStartOfDay(), ZoneOffset.ofHours(0)).toInstant();
        Instant periodEnd = ZonedDateTime.of(to.plusDays(1).atStartOfDay(), ZoneOffset.ofHours(0)).toInstant();

        return bookingRepository.findAllByBookingTimeBetween(periodStart, periodEnd).stream()
                .map(bookingDtoFactory::create)
                .collect(Collectors.toList());
    }

    private BookingDto save(BookingDto bookingDto) {
        return bookingDtoFactory.create(bookingRepository.saveBooking(bookingFactory.create(bookingDto)));
    }
}
