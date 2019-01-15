package uk.gov.cslearning.record.domain.factory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.record.domain.Booking;
import uk.gov.cslearning.record.domain.Learner;
import uk.gov.cslearning.record.dto.BookingCancellationReason;
import uk.gov.cslearning.record.dto.BookingDto;
import uk.gov.cslearning.record.dto.BookingStatus;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BookingFactoryTest {

    @Mock
    private EventFactory eventFactory;

    @Mock
    private LearnerFactory learnerFactory;

    @InjectMocks
    private BookingFactory bookingFactory;

    @Test
    public void shouldReturnBooking() throws URISyntaxException {
        int id = 99;
        URI paymentDetails = new URI("http://csrs/payment-details");
        Instant bookingTime = LocalDateTime.now().toInstant(ZoneOffset.UTC);
        Instant confirmationTime = LocalDateTime.now().toInstant(ZoneOffset.UTC);
        Instant cancellationTime = LocalDateTime.now().toInstant(ZoneOffset.UTC);
        String learnerUid = "learner-uuid";
        String learnerEmail = "test@domain.com";
        URI event = new URI("http://learning-catalogue/event-path");
        String accessibilityOptions = "Braille";
        BookingCancellationReason cancellationReason = BookingCancellationReason.PAYMENT;
        Learner learner = new Learner();
        learner.setId(99);

        BookingDto bookingDto = new BookingDto();

        bookingDto.setId(id);
        bookingDto.setPaymentDetails(paymentDetails);
        bookingDto.setBookingTime(bookingTime);
        bookingDto.setConfirmationTime(confirmationTime);
        bookingDto.setCancellationTime(cancellationTime);
        bookingDto.setLearner(learnerUid);
        bookingDto.setLearnerEmail(learnerEmail);
        bookingDto.setEvent(event);
        bookingDto.setStatus(BookingStatus.CONFIRMED);
        bookingDto.setAccessibilityOptions(accessibilityOptions);
        bookingDto.setCancellationReason(cancellationReason);

        when(learnerFactory.create(learnerUid, learnerEmail)).thenReturn(learner);

        Booking booking = bookingFactory.create(bookingDto);

        assertThat(booking.getStatus(), equalTo(BookingStatus.CONFIRMED));
        assertThat(booking.getBookingTime(), equalTo(bookingDto.getBookingTime()));
        assertThat(booking.getConfirmationTime(), equalTo(bookingDto.getConfirmationTime()));
        assertThat(booking.getCancellationTime(), equalTo(bookingDto.getCancellationTime()));
        assertThat(booking.getPaymentDetails(), equalTo(bookingDto.getPaymentDetails().getPath()));
        assertThat(booking.getId(), equalTo(bookingDto.getId()));
        assertThat(booking.getLearner().getId(), equalTo(99));
        assertThat(booking.getAccessibilityOptions(), equalTo(bookingDto.getAccessibilityOptions()));
        assertThat(booking.getCancellationReason(), equalTo(bookingDto.getCancellationReason()));

        verify(eventFactory).create(event.getPath());
        verify(learnerFactory).create(learnerUid, learnerEmail);
    }

    @Test
    public void shouldCreateLearnerIfNotPresent() throws URISyntaxException {
        int id = 99;
        URI paymentDetails = new URI("http://csrs/payment-details");
        Instant bookingTime = LocalDateTime.now().toInstant(ZoneOffset.UTC);
        Instant confirmationTime = LocalDateTime.now().toInstant(ZoneOffset.UTC);
        Instant cancellationTime = LocalDateTime.now().toInstant(ZoneOffset.UTC);
        String learnerUid = "learner-uuid";
        String learnerEmail = "test@domain.com";
        URI event = new URI("http://learning-catalogue/event-path");
        String accessibilityOptions = "Braille";
        BookingCancellationReason cancellationReason = BookingCancellationReason.PAYMENT;

        BookingDto bookingDto = new BookingDto();

        bookingDto.setId(id);
        bookingDto.setPaymentDetails(paymentDetails);
        bookingDto.setBookingTime(bookingTime);
        bookingDto.setConfirmationTime(confirmationTime);
        bookingDto.setCancellationTime(cancellationTime);
        bookingDto.setLearner(learnerUid);
        bookingDto.setLearnerEmail(learnerEmail);
        bookingDto.setEvent(event);
        bookingDto.setStatus(BookingStatus.CONFIRMED);
        bookingDto.setAccessibilityOptions(accessibilityOptions);
        bookingDto.setCancellationReason(cancellationReason);

        Booking booking = bookingFactory.create(bookingDto);

        assertThat(booking.getStatus(), equalTo(BookingStatus.CONFIRMED));
        assertThat(booking.getBookingTime(), equalTo(bookingDto.getBookingTime()));
        assertThat(booking.getConfirmationTime(), equalTo(bookingDto.getConfirmationTime()));
        assertThat(booking.getCancellationTime(), equalTo(bookingDto.getCancellationTime()));
        assertThat(booking.getPaymentDetails(), equalTo(bookingDto.getPaymentDetails().getPath()));
        assertThat(booking.getId(), equalTo(bookingDto.getId()));
        assertThat(booking.getAccessibilityOptions(), equalTo(bookingDto.getAccessibilityOptions()));
        assertThat(booking.getCancellationReason(), equalTo(bookingDto.getCancellationReason()));

        verify(learnerFactory).create(learnerUid, learnerEmail);
        verify(eventFactory).create(event.getPath());
    }

    @Test
    public void shouldIgnorePaymentDetailsIfNotPresent() throws URISyntaxException {
        int id = 99;
        Instant bookingTime = LocalDateTime.now().toInstant(ZoneOffset.UTC);
        Instant confirmationTime = LocalDateTime.now().toInstant(ZoneOffset.UTC);
        Instant cancellationTime = LocalDateTime.now().toInstant(ZoneOffset.UTC);
        String learnerUid = "learner-uuid";
        String learnerEmail = "test@domain.com";
        URI event = new URI("http://learning-catalogue/event-path");
        String accessibilityOptions = "Braille";
        BookingCancellationReason cancellationReason = BookingCancellationReason.PAYMENT;

        BookingDto bookingDto = new BookingDto();

        bookingDto.setId(id);
        bookingDto.setBookingTime(bookingTime);
        bookingDto.setConfirmationTime(confirmationTime);
        bookingDto.setCancellationTime(cancellationTime);
        bookingDto.setLearner(learnerUid);
        bookingDto.setLearnerEmail(learnerEmail);
        bookingDto.setEvent(event);
        bookingDto.setStatus(BookingStatus.CONFIRMED);
        bookingDto.setAccessibilityOptions(accessibilityOptions);
        bookingDto.setCancellationReason(cancellationReason);

        Booking booking = bookingFactory.create(bookingDto);

        assertThat(booking.getStatus(), equalTo(BookingStatus.CONFIRMED));
        assertThat(booking.getBookingTime(), equalTo(bookingDto.getBookingTime()));
        assertThat(booking.getConfirmationTime(), equalTo(bookingDto.getConfirmationTime()));
        assertThat(booking.getCancellationTime(), equalTo(bookingDto.getCancellationTime()));
        assertThat(booking.getId(), equalTo(bookingDto.getId()));
        assertThat(booking.getAccessibilityOptions(), equalTo(bookingDto.getAccessibilityOptions()));
        assertThat(booking.getCancellationReason(), equalTo(bookingDto.getCancellationReason()));

        verify(learnerFactory).create(learnerUid, learnerEmail);
        verify(eventFactory).create(event.getPath());
    }
}