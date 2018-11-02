package uk.gov.cslearning.record.dto.factory;

import org.junit.Test;
import uk.gov.cslearning.record.domain.Booking;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.domain.Learner;
import uk.gov.cslearning.record.dto.BookingDto;
import uk.gov.cslearning.record.dto.BookingStatus;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

public class BookingDtoFactoryTest {
    private BookingDtoFactory bookingDtoFactory = new BookingDtoFactory();

    @Test
    public void shouldReturnBookingDto() {
        long bookingId = 99L;
        String status = "Confirmed";
        String paymentDetails = "payment-details";
        LocalDateTime bookingTime = LocalDateTime.now();

        String learnerUuid = "learner-uuid";
        Learner learner = new Learner();
        learner.setUuid(learnerUuid);

        String eventPath = "event-path";
        Event event = new Event();
        event.setPath(eventPath);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setStatus(status);
        booking.setPaymentDetails(paymentDetails);
        booking.setBookingTime(bookingTime);
        booking.setLearner(learner);
        booking.setEvent(event);

        BookingDto bookingDto = bookingDtoFactory.create(booking);

        assertThat(bookingDto.getId(), equalTo(bookingId));
        assertThat(bookingDto.getBookingTime(), equalTo(bookingTime));
        assertThat(bookingDto.getEvent(), equalTo(eventPath));
        assertThat(bookingDto.getLearner(), equalTo(learnerUuid));
        assertThat(bookingDto.getPaymentDetails(), equalTo(paymentDetails));
        assertThat(bookingDto.getStatus(), equalTo(BookingStatus.forValue(status)));
    }
}