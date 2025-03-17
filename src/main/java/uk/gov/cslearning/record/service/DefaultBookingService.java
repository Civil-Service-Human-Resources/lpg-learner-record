package uk.gov.cslearning.record.service;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.domain.Booking;
import uk.gov.cslearning.record.domain.BookingStatus;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.domain.Learner;
import uk.gov.cslearning.record.domain.factory.BookingFactory;
import uk.gov.cslearning.record.dto.BookingDto;
import uk.gov.cslearning.record.dto.BookingStatusDto;
import uk.gov.cslearning.record.dto.factory.BookingDtoFactory;
import uk.gov.cslearning.record.exception.BookingNotFoundException;
import uk.gov.cslearning.record.exception.EventNotFoundException;
import uk.gov.cslearning.record.notifications.dto.IMessageParams;
import uk.gov.cslearning.record.notifications.service.NotificationService;
import uk.gov.cslearning.record.repository.BookingRepository;
import uk.gov.cslearning.record.repository.EventRepository;
import uk.gov.cslearning.record.util.UtilService;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DefaultBookingService implements BookingService {

    private final UtilService utilService;
    private final BookingFactory bookingFactory;
    private final BookingDtoFactory bookingDtoFactory;
    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final MessageService messageService;
    private final NotificationService notificationService;

    public DefaultBookingService(UtilService utilService, BookingFactory bookingFactory,
                                 BookingDtoFactory bookingDtoFactory, BookingRepository bookingRepository,
                                 EventRepository eventRepository, MessageService messageService, NotificationService notificationService) {
        this.utilService = utilService;
        this.bookingFactory = bookingFactory;
        this.bookingDtoFactory = bookingDtoFactory;
        this.bookingRepository = bookingRepository;
        this.eventRepository = eventRepository;
        this.messageService = messageService;
        this.notificationService = notificationService;
    }

    @Override
    public Optional<BookingDto> find(int bookingId) {
        return bookingRepository.findById(bookingId).map(bookingDtoFactory::create);
    }

    @Override
    public Optional<BookingDto> find(String eventUid, String learnerUid) {
        return bookingRepository.findByEventUidAndLearnerUid(eventUid, learnerUid).map(bookingDtoFactory::create);
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
        return booking.map(bookingDtoFactory::create);
    }

    @Override
    public BookingDto updateStatus(int bookingId, BookingStatusDto bookingStatus) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException(bookingId));
        return update(booking, bookingStatus);
    }

    @Override
    public BookingDto updateStatus(String eventUid, String learnerUid, BookingStatusDto bookingStatus) {
        Booking booking = findActiveBookingByLearnerUidAndEventUid(learnerUid, eventUid).orElseThrow(() -> new BookingNotFoundException(eventUid, learnerUid));
        return update(booking, bookingStatus);
    }

    @Override
    public BookingDto create(String eventUid, BookingDto bookingDto) {
        Event event = eventRepository.findByUid(eventUid).orElseThrow(() -> new EventNotFoundException(eventUid));
        List<IMessageParams> notifications = new ArrayList<>();
        Booking booking = bookingFactory.create(bookingDto);
        event.addBooking(booking);
        bookingRepository.save(booking);
        if (bookingDto.getStatus().equals(BookingStatus.CONFIRMED)) {
            notifications.addAll(messageService.createBookedMessages(booking));
        } else {
            notifications.addAll(messageService.createRegisteredMessages(booking));
        }
        notificationService.send(notifications);
        return bookingDtoFactory.create(booking);
    }

    @Override
    public BookingDto update(Booking booking, BookingStatusDto bookingStatusDto) {
        List<IMessageParams> notifications = new ArrayList<>();
        Instant updateTimestamp = utilService.getNowInstant();
        booking.setStatus(bookingStatusDto.getStatus());
        if (bookingStatusDto.getStatus().equals(BookingStatus.CONFIRMED)) {
            booking.setConfirmationTime(updateTimestamp);
            notifications.addAll(messageService.createBookedMessages(booking));
        } else {
            booking.setCancellationTime(updateTimestamp);
            booking.setCancellationReason(bookingStatusDto.getCancellationReason());
            notifications.addAll(messageService.createCancelBookingMessages(booking));
        }
        bookingRepository.save(booking);
        notificationService.send(notifications);
        return bookingDtoFactory.create(booking);
    }

    @Override
    public Optional<Booking> findActiveBookingByEmailAndEvent(String learnerEmail, String eventUid) {
        return bookingRepository.findByLearnerEmailAndEventUid(learnerEmail, eventUid, List.of(BookingStatus.REQUESTED, BookingStatus.CONFIRMED));
    }

    @Override
    public Iterable<BookingDto> findAllForPeriod(LocalDate from, LocalDate to) {
        Instant periodStart = ZonedDateTime.of(from.atStartOfDay(), ZoneOffset.ofHours(0)).toInstant();
        Instant periodEnd = ZonedDateTime.of(to.plusDays(1).atStartOfDay(), ZoneOffset.ofHours(0)).toInstant();
        List<Booking> bookings = bookingRepository.findAllByBookingTimeBetween(periodStart, periodEnd);
        return bookingDtoFactory.createBulk(bookings);
    }

    private Optional<Booking> findActiveBookingByLearnerUidAndEventUid(String learnerUid, String eventUid) {
        return bookingRepository.findByEventUidAndLearnerUidAndStatusIn(eventUid, learnerUid, List.of(BookingStatus.REQUESTED, BookingStatus.CONFIRMED));
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
