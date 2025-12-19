package uk.gov.cslearning.record.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.cslearning.record.MockedTestConfiguration;
import uk.gov.cslearning.record.SpringTestConfiguration;
import uk.gov.cslearning.record.domain.BookingStatus;
import uk.gov.cslearning.record.dto.*;
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

@WebMvcTest(controllers = BookingController.class)
@Import({SpringTestConfiguration.class, MockedTestConfiguration.class})
@AutoConfigureMockMvc
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

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    public void shouldListAllBookingsOnEvent() throws Exception {
        BookingDto bookingDto1 = new BookingDto();
        bookingDto1.setId(11);
        bookingDto1.setEventUid("eventUid");
        BookingDto bookingDto2 = new BookingDto();
        bookingDto2.setId(21);
        bookingDto2.setEventUid("eventUid");

        ArrayList<BookingDto> bookings = new ArrayList<>();
        bookings.add(bookingDto1);
        bookings.add(bookingDto2);

        when(bookingService.listByEventUid("eventUid")).thenReturn(bookings);

        mockMvc.perform(
                        get("/event/eventUid/booking")
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
        String event = "eventUid";
        Instant bookingTime = LocalDateTime.now().toInstant(ZoneOffset.UTC);
        URI paymentDetails = new URI("payment-details");

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(bookingId);
        bookingDto.setLearner(learner);
        bookingDto.setStatus(status);
        bookingDto.setEventUid(event);
        bookingDto.setBookingTime(bookingTime);
        bookingDto.setPaymentDetails(paymentDetails);

        when(bookingService.find(bookingId)).thenReturn(Optional.of(bookingDto));

        mockMvc.perform(
                        get("/event/eventUid/booking/" + bookingId)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(bookingId)))
                .andExpect(jsonPath("$.learner", equalTo(learner)))
                .andExpect(jsonPath("$.status", equalTo(status.getValue())))
                .andExpect(jsonPath("$.eventUid", equalTo(event)))
                .andExpect(jsonPath("$.paymentDetails", equalTo(paymentDetails.toString())))
                .andExpect(jsonPath("$.bookingTime",
                        equalTo(DATE_TIME_FORMATTER.format(bookingTime))));
    }


    @Test
    public void shouldReturnBookingFromEventUidAndLearnerUid() throws Exception {

        int bookingId = 99;
        String learnerUid = "learner-uid";
        String eventUid = "eventUid-uid";
        BookingStatus status = BookingStatus.CONFIRMED;
        String event = "_event";
        Instant bookingTime = LocalDateTime.now().toInstant(ZoneOffset.UTC);
        URI paymentDetails = new URI("payment-details");

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(bookingId);
        bookingDto.setLearner(learnerUid);
        bookingDto.setStatus(status);
        bookingDto.setEventUid(event);
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
                .andExpect(jsonPath("$.eventUid", equalTo(event)))
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
        String eventUid = "eventUid-uid";
        String learnerUid = "learner-uid";

        when(bookingService.find(eventUid, learnerUid)).thenReturn(Optional.empty());

        mockMvc.perform(
                        get(String.format("/eventUid/%s/learner/%s", eventUid, learnerUid))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldCreateBooking() throws Exception {
        int bookingId = 99;
        String learner = "_learner";
        BookingStatus status = BookingStatus.CONFIRMED;
        Instant bookingTime = LocalDateTime.now().toInstant(ZoneOffset.UTC);
        String event = "event-id";
        URI paymentDetails = new URI("payment-details");

        BookingDto booking = new BookingDto();
        booking.setLearner(learner);
        booking.setStatus(status);
        booking.setEventUid(event);
        booking.setBookingTime(bookingTime);
        booking.setPaymentDetails(paymentDetails);

        BookingDto savedBooking = new BookingDto();
        savedBooking.setId(bookingId);
        savedBooking.setLearner(learner);
        savedBooking.setStatus(status);
        savedBooking.setEventUid(event);
        savedBooking.setBookingTime(bookingTime);
        savedBooking.setPaymentDetails(paymentDetails);

        String json = objectMapper.writeValueAsString(booking);

        when(bookingService.create(eq("event-id"), eq(booking))).thenReturn(savedBooking);

        EventDto eventDto = new EventDto();
        eventDto.setStatus(EventStatus.ACTIVE);

        when(eventService.findByUid("event-id", false)).thenReturn(eventDto);

        mockMvc.perform(
                        post("/event/event-id/booking/").with(csrf())
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("location", "http://localhost/event/event-id/booking/" + bookingId));
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
        booking.setEventUid("eventId");

        mockMvc.perform(
                        post("/event/blah/booking/").with(csrf())
                                .content(objectMapper.writeValueAsString(booking))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].error", equalTo("A booking requires a learner")))
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
        String event = "eventUid";
        String learner = "_learner";

        BookingDto booking = new BookingDto();
        booking.setId(bookingId);
        booking.setStatus(status);
        booking.setBookingTime(bookingTime);
        booking.setPaymentDetails(paymentDetails);
        booking.setEventUid(event);
        booking.setLearner(learner);

        BookingStatusDto bookingStatus = new BookingStatusDto(status, BookingCancellationReason.PAYMENT);

        when(bookingService.updateStatus(eq(bookingId), eq(bookingStatus))).thenReturn(booking);

        mockMvc.perform(
                        patch("/event/eventUid/booking/" + bookingId).with(csrf())
                                .content(objectMapper.writeValueAsString(bookingStatus))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(bookingId)))
                .andExpect(jsonPath("$.learner", equalTo(learner)))
                .andExpect(jsonPath("$.status", equalTo(status.getValue())))
                .andExpect(jsonPath("$.eventUid", equalTo(event)))
                .andExpect(jsonPath("$.paymentDetails", equalTo(paymentDetails.toString())))
                .andExpect(jsonPath("$.bookingTime",
                        equalTo(DATE_TIME_FORMATTER.format(bookingTime))));
    }

    @Test
    public void shouldReturnBadMessageWithInvalidStatus() throws Exception {
        int bookingId = 930;
        BookingStatus status = BookingStatus.REQUESTED;

        BookingStatusDto bookingStatus = new BookingStatusDto(status, BookingCancellationReason.PAYMENT);

        mockMvc.perform(
                        patch("/event/blah/booking/" + bookingId).with(csrf())
                                .content(objectMapper.writeValueAsString(bookingStatus))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].error", equalTo("Booking status cannot be updated to 'Requested'")))
                .andExpect(jsonPath("$.status", equalTo(400)))
                .andExpect(jsonPath("$.message", equalTo("Bad Request")));

        verifyNoInteractions(bookingService);
    }

    @Test
    public void shouldReturnBadRequestOnConstraintViolationException() throws Exception {
        String learner = "_learner";
        BookingStatus status = BookingStatus.CONFIRMED;
        Instant bookingTime = LocalDateTime.now().toInstant(ZoneOffset.UTC);
        String event = "eventuid";
        URI paymentDetails = new URI("payment-details");

        BookingDto booking = new BookingDto();
        booking.setLearner(learner);
        booking.setStatus(status);
        booking.setEventUid(event);
        booking.setBookingTime(bookingTime);
        booking.setPaymentDetails(paymentDetails);

        ConstraintViolationException exception = mock(ConstraintViolationException.class);
        SQLException sqlException = mock(SQLException.class);
        when(sqlException.getMessage()).thenReturn("sql-exception");
        when(exception.getSQLException()).thenReturn(sqlException);
        when(exception.toString()).thenReturn("constraint-violation");

        doThrow(exception).when(bookingService).create("eventuid", booking);

        mockMvc.perform(
                        post("/event/eventuid/booking/").with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(booking))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]", equalTo("Storage error")))
                .andExpect(jsonPath("$.status", equalTo(400)))
                .andExpect(jsonPath("$.message", equalTo("Bad Request")));
    }

    @Test
    public void shouldUpdateBookingWithEventUidAndLearnerUid() throws Exception {
        String learnerUid = "learner-uid";
        String eventUid = "eventUid-uid";
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
        String event = "event";

        BookingDto booking = new BookingDto();
        booking.setId(bookingId);
        booking.setStatus(status);
        booking.setBookingTime(bookingTime);
        booking.setPaymentDetails(paymentDetails);
        booking.setEventUid(event);
        booking.setLearner(learnerUid);

        BookingStatusDto bookingStatus = new BookingStatusDto(status, BookingCancellationReason.PAYMENT);

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
                .andExpect(jsonPath("$.eventUid", equalTo(event)))
                .andExpect(jsonPath("$.paymentDetails", equalTo(paymentDetails.toString())))
                .andExpect(jsonPath("$.bookingTime",
                        equalTo(DATE_TIME_FORMATTER.format(bookingTime))));

        verify(bookingService).updateStatus(eventUid, learnerUid, bookingStatus);
    }

    @Test
    public void shouldReturnActiveBooking() throws Exception {
        String eventUid = "eventUid-id";
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
