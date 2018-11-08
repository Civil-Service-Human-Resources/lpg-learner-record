package uk.gov.cslearning.record.domain.factory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.record.domain.Booking;
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
        String learnerUid = "learner-uuid";
        URI event = new URI("http://learning-catalogue/event-path");

        BookingDto bookingDto = new BookingDto();

        bookingDto.setId(id);
        bookingDto.setPaymentDetails(paymentDetails);
        bookingDto.setBookingTime(bookingTime);
        bookingDto.setLearner(learnerUid);
        bookingDto.setEvent(event);
        bookingDto.setStatus(BookingStatus.CONFIRMED);

        Booking booking = bookingFactory.create(bookingDto);

        assertThat(booking.getStatus(), equalTo(bookingDto.getStatus().getValue()));
        assertThat(booking.getBookingTime(), equalTo(bookingDto.getBookingTime()));
        assertThat(booking.getPaymentDetails(), equalTo(bookingDto.getPaymentDetails().getPath()));
        assertThat(booking.getId(), equalTo(bookingDto.getId()));

        verify(learnerFactory).create(learnerUid);
        verify(eventFactory).create(event.getPath());
    }
}