package uk.gov.cslearning.record.service;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.domain.Booking;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.domain.Learner;
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
import uk.gov.cslearning.record.service.booking.BookingNotificationService;
import uk.gov.cslearning.record.service.xapi.XApiService;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;
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
    private final BookingNotificationService bookingNotificationService;

    public DefaultBookingService(BookingFactory bookingFactory, BookingDtoFactory bookingDtoFactory, BookingRepository bookingRepository, EventRepository eventRepository, XApiService xApiService, NotificationService notificationService, MessageService messageService, BookingNotificationService bookingNotificationService) {
        this.bookingFactory = bookingFactory;
        this.bookingDtoFactory = bookingDtoFactory;
        this.bookingRepository = bookingRepository;
        this.eventRepository = eventRepository;
        this.xApiService = xApiService;
        this.notificationService = notificationService;
        this.messageService = messageService;
        this.bookingNotificationService = bookingNotificationService;
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
            List<Booking> bookings = event.get().getBookings();
            return bookingDtoFactory.createBulk(bookings);
        }
        return new ArrayList<>();
    }

    @Override
    public Optional<BookingDto> findByLearnerUidAndEventUid(String eventUid, String learnerUid) {
        Optional<Booking> booking = findActiveBookingByLearnerUidAndEventUid(learnerUid, eventUid);

        if (booking.isPresent()) {
            return Optional.of(bookingDtoFactory.create(booking.get()));
        }
        return Optional.empty();
    }

    /**
     * Saves or updates a booking object.
     * A booking will be requested/registered if a user is requesting a paid for course without valid PO.
     * A booking will be confirmed if user provides a valid PO,
     * or if an admin changes status to 'Booked' on lpg-management,
     * or if the face-to-face module is free.
     *
     * @param bookingDto - passed from client or retrieved from database
     * @return savedBookingDto - dto for persisted booking
     */
    @Override
    public BookingDto register(BookingDto bookingDto) {
        if (bookingDto.getStatus().equals(BookingStatus.CONFIRMED) || bookingDto.getStatus().equals(BookingStatus.CANCELLED)) {
            xApiService.approve(bookingDto);
        } else if (bookingDto.getStatus().equals(BookingStatus.REQUESTED)) {
            xApiService.register(bookingDto);
        }

        BookingDto savedBookingDto = save(bookingDto);

        if (bookingDto.getStatus().equals(BookingStatus.CONFIRMED) || bookingDto.getStatus().equals(BookingStatus.CANCELLED)) {
            bookingNotificationService.sendConfirmedNotifications(savedBookingDto);
        } else if (bookingDto.getStatus().equals(BookingStatus.REQUESTED)) {
            bookingNotificationService.sendRequestedNotifications(savedBookingDto);
        }

        return savedBookingDto;
    }

    @Override
    public BookingDto updateStatus(int bookingId, BookingStatusDto bookingStatus) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException(bookingId));

        return updateStatus(booking, bookingStatus);
    }

    @Override
    public BookingDto updateStatus(String eventUid, String learnerUid, BookingStatusDto bookingStatus) {
        Booking booking = findActiveBookingByLearnerUidAndEventUid(learnerUid, eventUid).orElseThrow(() -> new BookingNotFoundException(eventUid, learnerUid));

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
            bookingNotificationService.sendCancelledNotifications(bookingDto, bookingStatusDto);

            return unregister(bookingDto);
        }
    }

    @Override
    public BookingDto unregister(BookingDto bookingDto) {
        if (bookingDto.getStatus().equals(BookingStatus.CONFIRMED)
                || bookingDto.getStatus().equals(BookingStatus.REQUESTED)) {
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
    public Iterable<BookingDto> findAllForPeriod(LocalDate from, LocalDate to) {
        Instant periodStart = ZonedDateTime.of(from.atStartOfDay(), ZoneOffset.ofHours(0)).toInstant();
        Instant periodEnd = ZonedDateTime.of(to.plusDays(1).atStartOfDay(), ZoneOffset.ofHours(0)).toInstant();
        List<Booking> bookings = bookingRepository.findAllByBookingTimeBetween(periodStart, periodEnd);
        return bookingDtoFactory.createBulk(bookings);
    }

    private Optional<Booking> findActiveBookingByLearnerUidAndEventUid(String learnerUid, String eventUid) {
        List<BookingStatus> status = Arrays.asList(BookingStatus.REQUESTED, BookingStatus.CONFIRMED);

        return bookingRepository.findByEventUidLearnerUid(eventUid, learnerUid, status);
    }

    private BookingDto save(BookingDto bookingDto) {
        Booking booking = bookingFactory.create(bookingDto);
        Booking savedBooking = bookingRepository.saveBooking(booking);
        return bookingDtoFactory.create(savedBooking);
    }

    @Override
    public void deleteAllByLearner(Learner learner) {
        bookingRepository.deleteAllByLearner(learner);
    }

    @Override
    public void deleteAllByAge(Instant instant) {
        bookingRepository.deleteAllByBookingTimeBefore(instant);
    }
}
