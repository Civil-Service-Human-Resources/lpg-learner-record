package uk.gov.cslearning.record.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.cslearning.record.domain.Booking;
import uk.gov.cslearning.record.domain.BookingStatus;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.domain.factory.BookingFactory;
import uk.gov.cslearning.record.dto.BookingCancellationReason;
import uk.gov.cslearning.record.dto.BookingDto;
import uk.gov.cslearning.record.dto.BookingStatusDto;
import uk.gov.cslearning.record.dto.factory.BookingDtoFactory;
import uk.gov.cslearning.record.exception.BookingNotFoundException;
import uk.gov.cslearning.record.notifications.service.NotificationService;
import uk.gov.cslearning.record.repository.BookingRepository;
import uk.gov.cslearning.record.repository.EventRepository;
import uk.gov.cslearning.record.util.UtilService;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class DefaultBookingServiceTest {

    @Mock
    private UtilService utilService;
    @Mock
    private BookingFactory bookingFactory;
    @Mock
    private BookingDtoFactory bookingDtoFactory;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private MessageService messageService;

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
        String eventUid = "eventUid-uid";
        String learnerUid = "learner-uid";

        Booking booking = new Booking();
        BookingDto bookingDto = new BookingDto();

        when(bookingRepository.findByEventUidAndLearnerUid(eventUid, learnerUid)).thenReturn(Optional.of(booking));
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
        String eventUid = "eventUid-uid";
        String learnerUid = "learner-uid";

        when(bookingRepository.findByEventUidAndLearnerUid(eventUid, learnerUid)).thenReturn(Optional.empty());

        assertEquals(Optional.empty(), bookingService.find(eventUid, learnerUid));
    }

    @Test
    public void shouldListBookingsByEventUid() {
        String eventId = "test-eventUid-id";

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
        when(bookingDtoFactory.createBulk(any())).thenReturn(bookingDtos);

        assertEquals(bookingDtos, bookingService.listByEventUid(eventId));
    }

    @Test
    public void shouldThrowBookingNotFoundException() {
        int bookingId = 99;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        try {
            bookingService.updateStatus(bookingId, new BookingStatusDto(BookingStatus.CONFIRMED, BookingCancellationReason.PAYMENT));
            fail("Expected BookingNotFoundException");
        } catch (BookingNotFoundException e) {
            assertEquals("Booking does not exist with id: 99", e.getMessage());
        }
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

        List<Booking> bookings = Arrays.asList(booking1, booking2, booking3);
        List<BookingDto> bookingDtos = Arrays.asList(bookingDto1, bookingDto2, bookingDto3);

        when(bookingDtoFactory.createBulk(bookings)).thenReturn(bookingDtos);

        when(bookingRepository.findAllByBookingTimeBetween(eq(start), eq(end)))
                .thenReturn(bookings);

        assertEquals(bookingDtos, bookingService.findAllForPeriod(from, to));
    }

    @Test
    public void shouldReturnBookingForLearnerUidAndEventUid() {
        String learnerUid = "learner-id";
        String eventUid = "eventUid-uid";

        Booking booking = new Booking();
        BookingDto bookingDto = new BookingDto();

        List<BookingStatus> status = Arrays.asList(BookingStatus.REQUESTED, BookingStatus.CONFIRMED);

        when(bookingRepository.findByEventUidAndLearnerUidAndStatusIn(eventUid, learnerUid, status)).thenReturn(Optional.of(booking));
        when(bookingDtoFactory.create(booking)).thenReturn(bookingDto);

        assertEquals(bookingService.findByLearnerUidAndEventUid(eventUid, learnerUid), Optional.of(bookingDto));

        verify(bookingRepository).findByEventUidAndLearnerUidAndStatusIn(eventUid, learnerUid, status);
        verify(bookingDtoFactory).create(booking);
    }

}
