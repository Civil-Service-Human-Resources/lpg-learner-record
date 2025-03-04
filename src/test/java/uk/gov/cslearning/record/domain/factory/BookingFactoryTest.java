package uk.gov.cslearning.record.domain.factory;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.cslearning.record.domain.Booking;
import uk.gov.cslearning.record.domain.BookingStatus;
import uk.gov.cslearning.record.domain.Learner;
import uk.gov.cslearning.record.dto.BookingCancellationReason;
import uk.gov.cslearning.record.dto.BookingDto;
import uk.gov.cslearning.record.repository.LearnerRepository;
import uk.gov.cslearning.record.util.IUtilService;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class BookingFactoryTest {

    Instant now = LocalDateTime.of(2023, 1, 1, 10, 0).toInstant(ZoneOffset.UTC);
    @Mock
    private IUtilService utilService;
    @Mock
    private LearnerRepository learnerRepository;
    @InjectMocks
    private BookingFactory bookingFactory;

    @BeforeEach
    public void before() {
        when(utilService.getNowInstant()).thenReturn(now);
    }

    @Test
    public void shouldReturnBookingWithExistingLearner() throws URISyntaxException {
        int id = 99;
        URI paymentDetails = new URI("http://csrs/payment-details");
        Instant bookingTime = now;
        Instant confirmationTime = now;
        Instant cancellationTime = now;
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

        when(learnerRepository.findByUid(learnerUid)).thenReturn(Optional.of(new Learner(learnerUid, learnerEmail)));
        Booking booking = bookingFactory.create(bookingDto);

        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
        assertEquals(bookingDto.getBookingTime(), booking.getBookingTime());
        assertEquals(bookingDto.getConfirmationTime(), booking.getConfirmationTime());
        assertEquals(bookingDto.getPaymentDetails().getPath(), booking.getPaymentDetails());
        assertEquals("learner-uuid", booking.getLearner().getUid());
        assertEquals("test@domain.com", booking.getLearner().getLearnerEmail());
        assertEquals(bookingDto.getAccessibilityOptions(), booking.getAccessibilityOptions());
    }

    @Test
    public void shouldCreateLearnerIfNotPresent() throws URISyntaxException {
        int id = 99;
        URI paymentDetails = new URI("http://csrs/payment-details");
        Instant bookingTime = now;
        Instant confirmationTime = now;
        Instant cancellationTime = now;
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

        when(learnerRepository.findByUid(learnerUid)).thenReturn(Optional.empty());
        Booking booking = bookingFactory.create(bookingDto);

        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
        assertEquals(bookingDto.getBookingTime(), booking.getBookingTime());
        assertEquals(bookingDto.getConfirmationTime(), booking.getConfirmationTime());
        assertEquals(bookingDto.getPaymentDetails().getPath(), booking.getPaymentDetails());
        assertEquals(bookingDto.getAccessibilityOptions(), booking.getAccessibilityOptions());
        assertEquals(learnerUid, booking.getLearner().getUid());
        assertEquals(learnerEmail, booking.getLearner().getLearnerEmail());
    }

    @Test
    public void shouldIgnorePaymentDetailsIfNotPresent() throws URISyntaxException {
        int id = 99;
        Instant bookingTime = now;
        Instant confirmationTime = now;
        Instant cancellationTime = now;
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

        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
        assertEquals(bookingDto.getBookingTime(), booking.getBookingTime());
        assertEquals(bookingDto.getAccessibilityOptions(), booking.getAccessibilityOptions());
    }
}
