package uk.gov.cslearning.record.domain.factory;

import edu.emory.mathcs.backport.java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.record.domain.Booking;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.domain.Learner;
import uk.gov.cslearning.record.dto.BookingDto;
import uk.gov.cslearning.record.dto.BookingStatus;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
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
    public void shouldReturnBooking() {
        long id = 99L;
        String paymentDetails = "payment-details";
        LocalDateTime bookingTime = LocalDateTime.now();
        String learnerUuid = "learner-uuid";
        String eventPath = "event-path";

        BookingDto bookingDto = new BookingDto();

        bookingDto.setId(id);
        bookingDto.setPaymentDetails(paymentDetails);
        bookingDto.setBookingTime(bookingTime);
        bookingDto.setLearner(learnerUuid);
        bookingDto.setEvent(eventPath);
        bookingDto.setStatus(BookingStatus.APPROVED);

        Booking booking = bookingFactory.create(bookingDto);

        assertThat(booking.getStatus(), equalTo(bookingDto.getStatus().getValue()));
        assertThat(booking.getBookingTime(), equalTo(bookingDto.getBookingTime()));
        assertThat(booking.getPaymentDetails(), equalTo(bookingDto.getPaymentDetails()));
        assertThat(booking.getId(), equalTo(bookingDto.getId()));

        verify(learnerFactory).create(learnerUuid, booking);
        verify(eventFactory).create(eventPath, booking);
    }
}