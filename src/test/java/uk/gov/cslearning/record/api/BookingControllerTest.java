package uk.gov.cslearning.record.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import uk.gov.cslearning.record.dto.BookingDto;
import uk.gov.cslearning.record.dto.BookingStatus;
import uk.gov.cslearning.record.dto.BookingStatusDto;
import uk.gov.cslearning.record.dto.factory.ValidationErrorsFactory;
import uk.gov.cslearning.record.exception.BookingNotFoundException;
import uk.gov.cslearning.record.service.BookingService;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest({BookingController.class, ValidationErrorsFactory.class})
@WithMockUser(username = "user")
public class BookingControllerTest {
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.ofHours(0));

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    private ObjectMapper objectMapper;


    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
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
    public void shouldReturn404IfNotFound() throws Exception {
        int bookingId = 99;

        when(bookingService.find(bookingId)).thenReturn(Optional.empty());

        mockMvc.perform(
                get("/event/blah/booking/" + bookingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldCreateBooking() throws Exception {
        int bookingId = 99;
        String learner = "_learner";
        BookingStatus status = BookingStatus.CONFIRMED;
        Instant bookingTime = LocalDateTime.now().toInstant(ZoneOffset.UTC);
        URI event = new URI("http://event");
        URI paymentDetails = new URI("payment-details");

        BookingDto booking = new BookingDto();
        booking.setLearner(learner);
        booking.setStatus(status);
        booking.setEvent(event);
        booking.setBookingTime(bookingTime);
        booking.setPaymentDetails(paymentDetails);

        BookingDto savedBooking = new BookingDto();
        savedBooking.setId(bookingId);
        savedBooking.setLearner(learner);
        savedBooking.setStatus(status);
        savedBooking.setEvent(event);
        savedBooking.setBookingTime(bookingTime);
        savedBooking.setPaymentDetails(paymentDetails);

        String json = objectMapper.writeValueAsString(booking);

        when(bookingService.register(eq(booking))).thenReturn(savedBooking);

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

        mockMvc.perform(
                post("/event/blah/booking/").with(csrf())
                        .content(objectMapper.writeValueAsString(booking))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.size", equalTo(2)))
                .andExpect(jsonPath("$.errors[0].field", equalTo("event")))
                .andExpect(jsonPath("$.errors[0].details", equalTo("A booking requires an event")))
                .andExpect(jsonPath("$.errors[1].field", equalTo("learner")))
                .andExpect(jsonPath("$.errors[1].details", equalTo("A booking requires a learner")));

        verifyZeroInteractions(bookingService);
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

        BookingStatusDto bookingStatus = new BookingStatusDto(status);

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

        BookingStatusDto bookingStatus = new BookingStatusDto(status);

        mockMvc.perform(
                patch("/event/blah/booking/" + bookingId).with(csrf())
                        .content(objectMapper.writeValueAsString(bookingStatus))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.size", equalTo(1)))
                .andExpect(jsonPath("$.errors[0].field", equalTo("status")))
                .andExpect(jsonPath("$.errors[0].details", equalTo("Booking status cannot be updated to 'Requested'")));

        verifyZeroInteractions(bookingService);
    }
}