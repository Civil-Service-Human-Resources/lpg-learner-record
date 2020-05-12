package uk.gov.cslearning.record.api;

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
import uk.gov.cslearning.record.dto.factory.ErrorDtoFactory;
import uk.gov.cslearning.record.service.BookingService;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest({ReportController.class, ErrorDtoFactory.class})
@WithMockUser(username = "user")
public class ReportControllerTest {
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.ofHours(0));

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Test
    public void shouldListAllBookingsForPeriod() throws Exception {
        String learnerUid = "learner-uid";
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
        booking.setBookingReference("AB45C");

        when(bookingService.findAllForPeriod(LocalDate.parse("2018-01-01"), LocalDate.parse("2018-01-31")))
                .thenReturn(Collections.singletonList(booking));

        mockMvc.perform(
                get("/reporting/bookings")
                        .param("from", "2018-01-01")
                        .param("to", "2018-01-31")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", equalTo(bookingId)))
                .andExpect(jsonPath("$[0].learner", equalTo(learnerUid)))
                .andExpect(jsonPath("$[0].status", equalTo(status.getValue())))
                .andExpect(jsonPath("$[0].event", equalTo(event.toString())))
                .andExpect(jsonPath("$[0].paymentDetails", equalTo(paymentDetails.toString())))
                .andExpect(jsonPath("$[0].bookingTime",
                        equalTo(DATE_TIME_FORMATTER.format(bookingTime))));
    }
}