package uk.gov.cslearning.record.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.record.domain.Booking;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.domain.factory.BookingFactory;
import uk.gov.cslearning.record.dto.BookingDto;
import uk.gov.cslearning.record.dto.BookingStatus;
import uk.gov.cslearning.record.dto.BookingStatusDto;
import uk.gov.cslearning.record.dto.factory.BookingDtoFactory;
import uk.gov.cslearning.record.exception.BookingNotFoundException;
import uk.gov.cslearning.record.notifications.dto.MessageDto;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultBookingServiceTest {

    @Mock
    private BookingFactory bookingFactory;

    @Mock
    private BookingDtoFactory bookingDtoFactory;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private XApiService xApiService;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private MessageService messageService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private DefaultBookingService bookingService;

    @Test
    public void shouldFindBookingById() {
        int bookingId = 99;
        Booking booking = new Booking();
        BookingDto bookingDto = new BookingDto();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingDtoFactory.create(booking)).thenReturn(bookingDto);

        assertEquals(Optional.of(bookingDto), bookingService.find(bookingId));
    }

    @Test
    public void shouldFindBookingByEventUidAndLearnerUid() {
        String eventUid = "event-uid";
        String learnerUid = "learner-uid";

        Booking booking = new Booking();
        BookingDto bookingDto = new BookingDto();

        List<BookingStatus> status = Arrays.asList(BookingStatus.REQUESTED, BookingStatus.CONFIRMED, BookingStatus.CANCELLED);

        when(bookingRepository.findByEventUidLearnerUid(eventUid, learnerUid, status)).thenReturn(Optional.of(booking));
        when(bookingDtoFactory.create(booking)).thenReturn(bookingDto);

        assertEquals(Optional.of(bookingDto), bookingService.find(eventUid, learnerUid));
    }


    @Test
    public void shouldReturnEmptyOptionalIfBookingNotFound() {
        int bookingId = 99;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertEquals(Optional.empty(), bookingService.find(bookingId));
    }

    @Test
    public void shouldReturnEmptyOptionalIfBookingNotFoundByEventUidAndLearnerUid() {
        String eventUid = "event-uid";
        String learnerUid = "learner-uid";

        List<BookingStatus> status = Arrays.asList(BookingStatus.REQUESTED, BookingStatus.CONFIRMED, BookingStatus.CANCELLED);

        when(bookingRepository.findByEventUidLearnerUid(eventUid, learnerUid, status)).thenReturn(Optional.empty());

        assertEquals(Optional.empty(), bookingService.find(eventUid, learnerUid));
    }

    @Test
    public void shouldListBookingsByEventUid() {
        String eventId = "test-event-id";

        Booking booking1 = new Booking();
        booking1.setId(11);
        Booking booking2 = new Booking();
        booking2.setId(21);

        ArrayList<Booking> bookings = new ArrayList<>();
        bookings.add(booking1);
        bookings.add(booking2);

        BookingDto bookingDto1 = new BookingDto();
        bookingDto1.setId(11);
        BookingDto bookingDto2 = new BookingDto();
        bookingDto2.setId(21);

        ArrayList<BookingDto> bookingDtos = new ArrayList<>();
        bookingDtos.add(bookingDto1);
        bookingDtos.add(bookingDto2);

        Event event = new Event();
        event.setBookings(bookings);

        when(eventRepository.findByUid(eventId)).thenReturn(Optional.of(event));
        when(bookingDtoFactory.create(any())).thenReturn(bookingDto1).thenReturn(bookingDto2);

        assertEquals(bookingDtos, bookingService.listByEventUid(eventId));
    }

    @Test
    public void shouldRegisterAndSaveBooking() {
        BookingDto unsavedBookingDto = new BookingDto();
        unsavedBookingDto.setStatus(BookingStatus.CONFIRMED);
        Booking unsavedBooking = new Booking();
        BookingDto savedBookingDto = new BookingDto();
        Booking savedBooking = new Booking();

        MessageDto messageDto = new MessageDto();

        when(bookingFactory.create(unsavedBookingDto)).thenReturn(unsavedBooking);
        when(bookingRepository.saveBooking(unsavedBooking)).thenReturn(savedBooking);
        when(bookingDtoFactory.create(savedBooking)).thenReturn(savedBookingDto);
        when(messageService.createBookedMessage(unsavedBookingDto)).thenReturn(messageDto);
        when(notificationService.send(messageDto)).thenReturn(true);

        assertEquals(savedBookingDto, bookingService.register(unsavedBookingDto));

        InOrder order = inOrder(xApiService, bookingRepository, notificationService);

        order.verify(xApiService).register(unsavedBookingDto);
        order.verify(notificationService).send(messageDto);
        order.verify(bookingRepository).saveBooking(unsavedBooking);
    }

    @Test
    public void shouldSaveBookingButNotRegisterIfNotConfirmed() {
        BookingDto unsavedBookingDto = new BookingDto();
        unsavedBookingDto.setStatus(BookingStatus.REQUESTED);
        unsavedBookingDto.setLearner("test-uid");
        Booking unsavedBooking = new Booking();
        BookingDto savedBookingDto = new BookingDto();
        Booking savedBooking = new Booking();

        when(bookingFactory.create(unsavedBookingDto)).thenReturn(unsavedBooking);
        when(bookingRepository.saveBooking(unsavedBooking)).thenReturn(savedBooking);
        when(bookingDtoFactory.create(savedBooking)).thenReturn(savedBookingDto);

        assertEquals(savedBookingDto, bookingService.register(unsavedBookingDto));

        verifyZeroInteractions(xApiService);
        verify(bookingRepository).saveBooking(unsavedBooking);
    }

    @Test
    public void shouldUpdateBookingStatus() {
        int bookingId = 99;
        Booking booking = mock(Booking.class);
        Booking updatedBooking = mock(Booking.class);
        Booking savedBooking = mock(Booking.class);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setStatus(BookingStatus.REQUESTED);
        bookingDto.setLearner("test-uid");
        BookingDto savedBookingDto = new BookingDto();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        when(bookingDtoFactory.create(booking)).thenReturn(bookingDto);

        BookingStatusDto bookingStatus = new BookingStatusDto(BookingStatus.CONFIRMED, "");

        when(bookingFactory.create(bookingDto)).thenReturn(updatedBooking);
        when(bookingRepository.saveBooking(updatedBooking)).thenReturn(savedBooking);
        when(bookingDtoFactory.create(savedBooking)).thenReturn(savedBookingDto);

        assertEquals(savedBookingDto, bookingService.updateStatus(bookingId, bookingStatus));

        verify(xApiService).register(bookingDto);
    }

    @Test
    public void shouldUpdateBookingStatusWithEventUidAndLearnerUid() {
        String eventUid = "event-uid";
        String learnerUid = "learner-uid";

        Booking booking = mock(Booking.class);
        Booking updatedBooking = mock(Booking.class);
        Booking savedBooking = mock(Booking.class);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setStatus(BookingStatus.REQUESTED);
        bookingDto.setLearner("test-uid");
        BookingDto savedBookingDto = new BookingDto();

        List<BookingStatus> status = Arrays.asList(BookingStatus.REQUESTED, BookingStatus.CONFIRMED);

        when(bookingRepository.findByEventUidLearnerUid(eventUid, learnerUid, status)).thenReturn(Optional.of(booking));

        when(bookingDtoFactory.create(booking)).thenReturn(bookingDto);

        BookingStatusDto bookingStatus = new BookingStatusDto(BookingStatus.CONFIRMED, "");

        when(bookingFactory.create(bookingDto)).thenReturn(updatedBooking);
        when(bookingRepository.saveBooking(updatedBooking)).thenReturn(savedBooking);
        when(bookingDtoFactory.create(savedBooking)).thenReturn(savedBookingDto);

        assertEquals(savedBookingDto, bookingService.updateStatus(eventUid, learnerUid, bookingStatus));

        verify(xApiService).register(bookingDto);
    }

    @Test
    public void shouldThrowBookingNotFoundException() {
        int bookingId = 99;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        try {
            bookingService.updateStatus(bookingId, new BookingStatusDto(BookingStatus.CONFIRMED, ""));
            fail("Expected BookingNotFoundException");
        } catch (BookingNotFoundException e) {
            assertEquals("Booking does not exist with id: 99", e.getMessage());
        }
    }

    @Test
    public void shouldUnregisterBookingWithBookingDto() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStatus(BookingStatus.CONFIRMED);
        BookingDto savedBookingDto = new BookingDto();

        Booking booking = new Booking();
        Booking savedBooking = new Booking();

        when(bookingFactory.create(bookingDto)).thenReturn(booking);
        when(bookingRepository.saveBooking(booking)).thenReturn(savedBooking);
        when(bookingDtoFactory.create(savedBooking)).thenReturn(savedBookingDto);

        assertEquals(savedBookingDto, bookingService.unregister(bookingDto));

        verify(xApiService).unregister(bookingDto);
        verify(bookingRepository).saveBooking(booking);
    }

    @Test
    public void shouldUnregisterBookingWithBooking() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStatus(BookingStatus.CONFIRMED);
        BookingDto savedBookingDto = new BookingDto();

        Booking booking1 = new Booking();
        booking1.setId(1);
        Booking booking2 = new Booking();
        booking2.setId(2);

        Booking savedBooking = new Booking();

        MessageDto messageDto = new MessageDto();

        when(messageService.createCancelEventMessage(booking1, "cancellation reason")).thenReturn(messageDto);
        when(notificationService.send(messageDto)).thenReturn(true);
        when(bookingDtoFactory.create(booking1)).thenReturn(bookingDto);
        when(bookingFactory.create(bookingDto)).thenReturn(booking2);
        when(bookingRepository.saveBooking(booking2)).thenReturn(savedBooking);
        when(bookingDtoFactory.create(savedBooking)).thenReturn(savedBookingDto);

        assertEquals(savedBookingDto, bookingService.unregister(booking1, "cancellation reason"));

        verify(messageService).createCancelEventMessage(booking1, "cancellation reason");
        verify(notificationService).send(messageDto);
        verify(xApiService).unregister(bookingDto);
        verify(bookingRepository).saveBooking(booking2);
    }

    @Test
    public void shouldNotCallXApiIfStatusIsRequested() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStatus(BookingStatus.REQUESTED);
        Booking booking = new Booking();
        BookingDto savedBookingDto = new BookingDto();
        Booking savedBooking = new Booking();

        when(bookingFactory.create(bookingDto)).thenReturn(booking);
        when(bookingRepository.saveBooking(booking)).thenReturn(savedBooking);
        when(bookingDtoFactory.create(savedBooking)).thenReturn(savedBookingDto);

        assertEquals(savedBookingDto, bookingService.register(bookingDto));

        verifyZeroInteractions(xApiService);
        verify(bookingRepository).saveBooking(booking);
    }

    @Test
    public void shouldReturnLearnerIfBooked() {
        String learnerEmail = "test@domain.com";
        String eventUid = "eventUid";
        Booking booking = new Booking();
        List<BookingStatus> status = Arrays.asList(BookingStatus.REQUESTED, BookingStatus.CONFIRMED);

        when(bookingRepository.findByLearnerEmailAndEventUid(learnerEmail, eventUid, status)).thenReturn(Optional.of(booking));

        assertEquals(Optional.of(booking), bookingService.findActiveBookingByEmailAndEvent(learnerEmail, eventUid));
    }

    @Test
    public void shouldListAllBookings() {
        Booking booking1 = new Booking();
        Booking booking2 = new Booking();
        Booking booking3 = new Booking();

        BookingDto bookingDto1 = new BookingDto();
        BookingDto bookingDto2 = new BookingDto();
        BookingDto bookingDto3 = new BookingDto();

        when(bookingDtoFactory.create(booking1)).thenReturn(bookingDto1);
        when(bookingDtoFactory.create(booking2)).thenReturn(bookingDto2);
        when(bookingDtoFactory.create(booking3)).thenReturn(bookingDto3);

        when(bookingRepository.findAll()).thenReturn(Arrays.asList(booking1, booking2, booking3));

        assertEquals(Arrays.asList(bookingDto1, bookingDto2, bookingDto3), bookingService.findAll());
    }

    @Test
    public void shouldListAllBookingsForPeriod() {
        LocalDate from = LocalDate.parse("2018-01-01");
        LocalDate to = LocalDate.parse("2018-01-07");

        Instant start = Instant.parse("2018-01-01T00:00:00Z");
        Instant end = Instant.parse("2018-01-08T00:00:00Z");

        Booking booking1 = new Booking();
        Booking booking2 = new Booking();
        Booking booking3 = new Booking();

        BookingDto bookingDto1 = new BookingDto();
        BookingDto bookingDto2 = new BookingDto();
        BookingDto bookingDto3 = new BookingDto();

        when(bookingDtoFactory.create(booking1)).thenReturn(bookingDto1);
        when(bookingDtoFactory.create(booking2)).thenReturn(bookingDto2);
        when(bookingDtoFactory.create(booking3)).thenReturn(bookingDto3);

        when(bookingRepository.findAllByBookingTimeBetween(eq(start), eq(end)))
                .thenReturn(Arrays.asList(booking1, booking2, booking3));

        assertEquals(Arrays.asList(bookingDto1, bookingDto2, bookingDto3), bookingService.findAllForPeriod(from, to));
    }
}