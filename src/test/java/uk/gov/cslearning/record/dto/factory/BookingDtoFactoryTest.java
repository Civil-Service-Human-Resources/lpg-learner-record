package uk.gov.cslearning.record.dto.factory;

import org.junit.Test;
import uk.gov.cslearning.record.domain.Booking;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.domain.Learner;
import uk.gov.cslearning.record.dto.BookingDto;
import uk.gov.cslearning.record.dto.BookingStatus;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

public class BookingDtoFactoryTest {
    private final String learningCatalogueBaseUri = "http://learning-catalogue";
    private final String csrsBaseUri = "http://csrs";

    private BookingDtoFactory bookingDtoFactory = new BookingDtoFactory(learningCatalogueBaseUri, csrsBaseUri);

    @Test
    public void shouldReturnBookingDto() throws URISyntaxException {
        int bookingId = 99;
        String status = "Confirmed";
        String paymentDetails = "/purchase-order/abcde12345";
        Instant bookingTime = LocalDateTime.now().toInstant(ZoneOffset.UTC);

        String learnerUuid = "learner-uuid";
        String learnerEmail = "test@domain.com";
        Learner learner = new Learner();
        learner.setUid(learnerUuid);
        learner.setLearnerEmail(learnerEmail);

        String eventPath = "/courses/abc/modules/def/events/ghi";
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
        assertThat(bookingDto.getEvent(),
                equalTo(new URI(String.join("", learningCatalogueBaseUri, eventPath))));

        assertThat(bookingDto.getLearner(), equalTo(learnerUuid));
        assertThat(bookingDto.getLearnerEmail(), equalTo(learnerEmail));
        assertThat(bookingDto.getPaymentDetails(),
                equalTo(new URI(String.join("", csrsBaseUri, paymentDetails))));

        assertThat(bookingDto.getStatus(), equalTo(BookingStatus.forValue(status)));
    }

    @Test
    public void shouldIgnoreMissingPaymentDetails() throws URISyntaxException {
        int bookingId = 99;
        String status = "Confirmed";
        Instant bookingTime = LocalDateTime.now().toInstant(ZoneOffset.UTC);

        String learnerUuid = "learner-uuid";
        String learnerEmail = "test@domain.com";
        Learner learner = new Learner();
        learner.setUid(learnerUuid);
        learner.setLearnerEmail(learnerEmail);

        String eventPath = "/courses/abc/modules/def/events/ghi";
        Event event = new Event();
        event.setPath(eventPath);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setStatus(status);
        booking.setBookingTime(bookingTime);
        booking.setLearner(learner);
        booking.setEvent(event);

        BookingDto bookingDto = bookingDtoFactory.create(booking);

        assertThat(bookingDto.getId(), equalTo(bookingId));
        assertThat(bookingDto.getBookingTime(), equalTo(bookingTime));
        assertThat(bookingDto.getEvent(),
                equalTo(new URI(String.join("", learningCatalogueBaseUri, eventPath))));

        assertThat(bookingDto.getLearner(), equalTo(learnerUuid));
        assertThat(bookingDto.getLearnerEmail(), equalTo(learnerEmail));

        assertThat(bookingDto.getStatus(), equalTo(BookingStatus.forValue(status)));
    }
}