package uk.gov.cslearning.record.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.cslearning.record.dto.*;
import uk.gov.cslearning.record.dto.factory.ErrorDtoFactory;
import uk.gov.cslearning.record.service.BookingService;
import uk.gov.cslearning.record.service.EventService;

import java.net.URI;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest({BookingController.class, ErrorDtoFactory.class})
@WithMockUser(username = "user")
public class BookingControllerTest {
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.ofHours(0));

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private EventService eventService;

    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    public void shouldListAllBookingsOnEvent() throws Exception {
        BookingDto bookingDto1 = new BookingDto();
        bookingDto1.setId(11);
        bookingDto1.setEvent(URI.create("http://path/to/eventUid"));
        BookingDto bookingDto2 = new BookingDto();
        bookingDto2.setId(21);
        bookingDto2.setEvent(URI.create("http://path/to/eventUid"));

        ArrayList<BookingDto> bookings = new ArrayList<>();
        bookings.add(bookingDto1);
        bookings.add(bookingDto2);

        when(bookingService.listByEventUid("test-event-id")).thenReturn(bookings);

        mockMvc.perform(
                get("/event/test-event-id/booking")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", equalTo(11)))
                .andExpect(jsonPath("$[1].id", equalTo(21)));
    }

    @Test
    public void shouldReturnEmptyArrayIfNoBookingsOnEvent() throws Exception {
        when(bookingService.listByEventUid("test-event-id")).thenReturn(new ArrayList<>());

        mockMvc.perform(
                get("/event/test-event-id/booking")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", equalTo(new ArrayList<>())));
    }

    @Test
    public void shouldReturnBookingIfFound() throws Exception {
        int bookingId = 99;
        String learner = "_learner";
        BookingStatus status = BookingStatus.CONFIRMED;
        URI event = new URI("_event");
        Instant bookingTime = LocalDateTime.now().toInstant(ZoneOffset.UTC);
        URI paymentDetails = new URI("payment-details");

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(bookingId);
        bookingDto.setLearner(learner);
        bookingDto.setStatus(status);
        bookingDto.setEvent(event);
        bookingDto.setBookingTime(bookingTime);
        bookingDto.setPaymentDetails(paymentDetails);

        when(bookingService.find(bookingId)).thenReturn(Optional.of(bookingDto));

        mockMvc.perform(
                get("/event/blah/booking/" + bookingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(bookingId)))
                .andExpect(jsonPath("$.learner", equalTo(learner)))
                .andExpect(jsonPath("$.status", equalTo(status.getValue())))
                .andExpect(jsonPath("$.event", equalTo(event.toString())))
                .andExpect(jsonPath("$.paymentDetails", equalTo(paymentDetails.toString())))
                .andExpect(jsonPath("$.bookingTime",
                        equalTo(DATE_TIME_FORMATTER.format(bookingTime))));
    }


    @Test
    public void shouldReturnBookingFromEventUidAndLearnerUid() throws Exception {

        int bookingId = 99;
        String learnerUid = "learner-uid";
        String eventUid = "event-uid";
        BookingStatus status = BookingStatus.CONFIRMED;
        URI event = new URI("_event");
        Instant bookingTime = LocalDateTime.now().toInstant(ZoneOffset.UTC);
        URI paymentDetails = new URI("payment-details");

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(bookingId);
        bookingDto.setLearner(learnerUid);
        bookingDto.setStatus(status);
        bookingDto.setEvent(event);
        bookingDto.setBookingTime(bookingTime);
        bookingDto.setPaymentDetails(paymentDetails);

        when(bookingService.find(eventUid, learnerUid)).thenReturn(Optional.of(bookingDto));

        mockMvc.perform(
                get(String.format("/event/%s/learner/%s", eventUid, learnerUid))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(bookingId)))
                .andExpect(jsonPath("$.learner", equalTo(learnerUid)))
                .andExpect(jsonPath("$.status", equalTo(status.getValue())))
                .andExpect(jsonPath("$.event", equalTo(event.toString())))
                .andExpect(jsonPath("$.paymentDetails", equalTo(paymentDetails.toString())))
                .andExpect(jsonPath("$.bookingTime",
                        equalTo(DATE_TIME_FORMATTER.format(bookingTime))));
    }

    @Test
    public void shouldReturn404IfNotFound() throws Exception {
        int bookingId = 99;

        when(bookingService.find(bookingId)).thenReturn(Optional.empty());

        mockMvc.perform(
                get("/event/blah/booking/" + bookingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturn404IfNotFoundFromEventUidAndLearnerUid() throws Exception {
        String eventUid = "event-uid";
        String learnerUid = "learner-uid";

        when(bookingService.find(eventUid, learnerUid)).thenReturn(Optional.empty());

        mockMvc.perform(
                get(String.format("/event/%s/learner/%s", eventUid, learnerUid))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldCreateBooking() throws Exception {
        int bookingId = 99;
        String learner = "_learner";
        String learnerEmail = "test@domain.com";
        BookingStatus status = BookingStatus.CONFIRMED;
        Instant bookingTime = LocalDateTime.now().toInstant(ZoneOffset.UTC);
        URI event = new URI("http://example.org/path/to/event/event-id");
        URI paymentDetails = new URI("payment-details");

        BookingDto booking = new BookingDto();
        booking.setLearner(learner);
        booking.setLearnerEmail(learnerEmail);
        booking.setStatus(status);
        booking.setEvent(event);
        booking.setBookingTime(bookingTime);
        booking.setPaymentDetails(paymentDetails);

        BookingDto savedBooking = new BookingDto();
        savedBooking.setId(bookingId);
        savedBooking.setLearner(learner);
        savedBooking.setLearnerEmail(learnerEmail);
        savedBooking.setStatus(status);
        savedBooking.setEvent(event);
        savedBooking.setBookingTime(bookingTime);
        savedBooking.setPaymentDetails(paymentDetails);

        String json = objectMapper.writeValueAsString(booking);

        when(bookingService.register(eq(booking))).thenReturn(savedBooking);

        EventDto eventDto = new EventDto();
        eventDto.setStatus(EventStatus.ACTIVE);

        when(eventService.findByUid("event-id")).thenReturn(Optional.of(eventDto));

        mockMvc.perform(
                post("/event/blah/booking/").with(csrf())
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("location", "http://localhost/event/blah/booking/" + bookingId));
    }

    @Test
    public void shouldReturnBadRequestIfLearnerOrEventIsMissing() throws Exception {
        BookingStatus status = BookingStatus.CONFIRMED;
        Instant bookingTime = LocalDateTime.now().toInstant(ZoneOffset.UTC);
        URI paymentDetails = new URI("payment-details");

        BookingDto booking = new BookingDto();
        booking.setStatus(status);
        booking.setBookingTime(bookingTime);
        booking.setPaymentDetails(paymentDetails);
        booking.setEvent(new URI("test/path/to/eventId"));

        mockMvc.perform(
                post("/event/blah/booking/").with(csrf())
                        .content(objectMapper.writeValueAsString(booking))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]", equalTo("A booking requires a learner")))
                .andExpect(jsonPath("$.errors[1]", equalTo("A booking requires a learner email address")))
                .andExpect(jsonPath("$.status", equalTo(400)))
                .andExpect(jsonPath("$.message", equalTo("Bad Request")));

    }

    @Test
    public void shouldUpdateBooking() throws Exception {
        int bookingId = 930;
        BookingStatus status = BookingStatus.CONFIRMED;
        Instant bookingTime =
                LocalDateTime.of(2018,
                        1,
                        1,
                        13,
                        59,
                        12,
                        500).toInstant(ZoneOffset.UTC);
        URI paymentDetails = new URI("payment-details");
        URI event = new URI("http://event");
        String learner = "_learner";

        BookingDto booking = new BookingDto();
        booking.setId(bookingId);
        booking.setStatus(status);
        booking.setBookingTime(bookingTime);
        booking.setPaymentDetails(paymentDetails);
        booking.setEvent(event);
        booking.setLearner(learner);

        BookingStatusDto bookingStatus = new BookingStatusDto(status, "");

        when(bookingService.updateStatus(eq(bookingId), eq(bookingStatus))).thenReturn(booking);

        mockMvc.perform(
                patch("/event/blah/booking/" + bookingId).with(csrf())
                        .content(objectMapper.writeValueAsString(bookingStatus))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(bookingId)))
                .andExpect(jsonPath("$.learner", equalTo(learner)))
                .andExpect(jsonPath("$.status", equalTo(status.getValue())))
                .andExpect(jsonPath("$.event", equalTo(event.toString())))
                .andExpect(jsonPath("$.paymentDetails", equalTo(paymentDetails.toString())))
                .andExpect(jsonPath("$.bookingTime",
                        equalTo(DATE_TIME_FORMATTER.format(bookingTime))));
    }

    @Test
    public void shouldReturnBadMessageWithInvalidStatus() throws Exception {
        int bookingId = 930;
        BookingStatus status = BookingStatus.REQUESTED;

        BookingStatusDto bookingStatus = new BookingStatusDto(status, "");

        mockMvc.perform(
                patch("/event/blah/booking/" + bookingId).with(csrf())
                        .content(objectMapper.writeValueAsString(bookingStatus))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]", equalTo("Booking status cannot be updated to 'Requested'")))
                .andExpect(jsonPath("$.status", equalTo(400)))
                .andExpect(jsonPath("$.message", equalTo("Bad Request")));

        verifyZeroInteractions(bookingService);
    }

    @Test
    public void shouldReturnBadRequestOnConstraintViolationException() throws Exception {
        String learner = "_learner";
        String learnerEmail = "test@domain.com";
        BookingStatus status = BookingStatus.CONFIRMED;
        Instant bookingTime = LocalDateTime.now().toInstant(ZoneOffset.UTC);
        URI event = new URI("http://path/to/eventuid");
        URI paymentDetails = new URI("payment-details");

        BookingDto booking = new BookingDto();
        booking.setLearner(learner);
        booking.setLearnerEmail(learnerEmail);
        booking.setStatus(status);
        booking.setEvent(event);
        booking.setBookingTime(bookingTime);
        booking.setPaymentDetails(paymentDetails);

        ConstraintViolationException exception = mock(ConstraintViolationException.class);
        SQLException sqlException = mock(SQLException.class);
        when(sqlException.getMessage()).thenReturn("sql-exception");
        when(exception.getSQLException()).thenReturn(sqlException);
        when(exception.toString()).thenReturn("constraint-violation");

        doThrow(exception).when(bookingService).register(booking);

        mockMvc.perform(
                post("/event/blah/booking/").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]", equalTo("Storage error")))
                .andExpect(jsonPath("$.status", equalTo(400)))
                .andExpect(jsonPath("$.message", equalTo("Bad Request")));
    }

    @Test
    public void shouldReturnBadRequestIfEventIsCancelled() throws Exception {
        String learner = "_learner";
        String learnerEmail = "test@domain.com";
        BookingStatus status = BookingStatus.CONFIRMED;
        Instant bookingTime = LocalDateTime.now().toInstant(ZoneOffset.UTC);
        URI event = new URI("http://example.org/path/to/event/event-id");
        URI paymentDetails = new URI("payment-details");

        BookingDto booking = new BookingDto();
        booking.setLearner(learner);
        booking.setLearnerEmail(learnerEmail);
        booking.setStatus(status);
        booking.setEvent(event);
        booking.setBookingTime(bookingTime);
        booking.setPaymentDetails(paymentDetails);

        EventDto eventDto = new EventDto();
        eventDto.setStatus(EventStatus.CANCELLED);

        when(eventService.findByUid("event-id")).thenReturn(Optional.of(eventDto));

        mockMvc.perform(
                post("/event/blah/booking/").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]", equalTo("Cannot apply booking to a cancelled event.")))
                .andExpect(jsonPath("$.status", equalTo(400)))
                .andExpect(jsonPath("$.message", equalTo("Bad Request")));
    }

    @Test
    public void shouldUpdateBookingWithEventUidAndLearnerUid() throws Exception {
        String learnerUid = "learner-uid";
        String eventUid = "event-uid";
        int bookingId = 99;
        BookingStatus status = BookingStatus.CONFIRMED;
        Instant bookingTime =
                LocalDateTime.of(2018,
                        1,
                        1,
                        13,
                        59,
                        12,
                        500).toInstant(ZoneOffset.UTC);
        URI paymentDetails = new URI("payment-details");
        URI event = new URI("http://event");

        BookingDto booking = new BookingDto();
        booking.setId(bookingId);
        booking.setStatus(status);
        booking.setBookingTime(bookingTime);
        booking.setPaymentDetails(paymentDetails);
        booking.setEvent(event);
        booking.setLearner(learnerUid);

        BookingStatusDto bookingStatus = new BookingStatusDto(status, "");

        when(bookingService.updateStatus(eq(eventUid), eq(learnerUid), eq(bookingStatus))).thenReturn(booking);

        mockMvc.perform(
                patch(String.format("/event/%s/learner/%s", eventUid, learnerUid)).with(csrf())
                        .content(objectMapper.writeValueAsString(bookingStatus))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(bookingId)))
                .andExpect(jsonPath("$.learner", equalTo(learnerUid)))
                .andExpect(jsonPath("$.status", equalTo(status.getValue())))
                .andExpect(jsonPath("$.event", equalTo(event.toString())))
                .andExpect(jsonPath("$.paymentDetails", equalTo(paymentDetails.toString())))
                .andExpect(jsonPath("$.bookingTime",
                        equalTo(DATE_TIME_FORMATTER.format(bookingTime))));

        verify(bookingService).updateStatus(eventUid, learnerUid, bookingStatus);
    }

    @Test
    public void shouldReturnActiveBooking() throws Exception {
        String eventUid = "event-id";
        String learnerUid = "learner-id";
        BookingDto booking = new BookingDto();
        booking.setId(1);

        when(bookingService.findByLearnerUidAndEventUid(eventUid, learnerUid)).thenReturn(Optional.of(booking));

        mockMvc.perform((
                get(String.format("/event/%s/booking/%s/active", eventUid, learnerUid)).with(csrf()))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)));

        verify(bookingService).findByLearnerUidAndEventUid(eventUid, learnerUid);
    }
}