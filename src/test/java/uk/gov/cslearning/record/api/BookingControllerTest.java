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
import uk.gov.cslearning.record.dto.factory.ValidationErrorsFactory;
import uk.gov.cslearning.record.service.BookingService;

import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsArrayContainingInAnyOrder.arrayContainingInAnyOrder;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest({BookingController.class, ValidationErrorsFactory.class})
@WithMockUser(username = "user")
public class BookingControllerTest {

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
        long bookingId = 99;
        String learner = "_learner";
        BookingStatus status = BookingStatus.CONFIRMED;
        URI event = new URI("_event");
        LocalDateTime bookingTime = LocalDateTime.now();
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
                .andExpect(jsonPath("$.id", equalTo((int)bookingId)))
                .andExpect(jsonPath("$.learner", equalTo(learner)))
                .andExpect(jsonPath("$.status", equalTo(status.getValue())))
                .andExpect(jsonPath("$.event", equalTo(event.toString())))
                .andExpect(jsonPath("$.paymentDetails", equalTo(paymentDetails.toString())))
                .andExpect(jsonPath("$.bookingTime",
                        equalTo(bookingTime.format(DateTimeFormatter.ISO_DATE_TIME))));
    }

    @Test
    public void shouldReturn404IfNotFound() throws Exception {
        long bookingId = 99;

        when(bookingService.find(bookingId)).thenReturn(Optional.empty());

        mockMvc.perform(
                get("/event/blah/booking/" + bookingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldCreateBooking() throws Exception {
        long bookingId = 99;
        String learner = "_learner";
        BookingStatus status = BookingStatus.CONFIRMED;
        URI event = new URI("http://event");
        LocalDateTime bookingTime = LocalDateTime.now();
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

        when(bookingService.save(eq(booking))).thenReturn(savedBooking);

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
        LocalDateTime bookingTime = LocalDateTime.now();
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
}