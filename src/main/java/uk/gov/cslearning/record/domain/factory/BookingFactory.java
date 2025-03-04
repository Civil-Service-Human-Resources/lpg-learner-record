package uk.gov.cslearning.record.domain.factory;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.domain.Booking;
import uk.gov.cslearning.record.domain.BookingStatus;
import uk.gov.cslearning.record.domain.Learner;
import uk.gov.cslearning.record.dto.BookingDto;
import uk.gov.cslearning.record.util.IUtilService;

import java.time.Instant;

@Component
public class BookingFactory {

    private final IUtilService utilService;

    public BookingFactory(IUtilService utilService) {
        this.utilService = utilService;
    }

    public Booking create(BookingDto bookingDto) {
        Instant creationTime = utilService.getNowInstant();
        Booking booking = new Booking();

        booking.setStatus(bookingDto.getStatus());
        booking.setBookingTime(creationTime);
        if (bookingDto.getStatus().equals(BookingStatus.CONFIRMED)) {
            booking.setConfirmationTime(creationTime);
        }
        if (null != bookingDto.getPaymentDetails()) {
            booking.setPaymentDetails(bookingDto.getPaymentDetails().getPath());
        }
        booking.setLearner(new Learner(bookingDto.getLearner(), bookingDto.getLearnerEmail()));
        booking.setPoNumber(bookingDto.getPoNumber());
        booking.setAccessibilityOptions(bookingDto.getAccessibilityOptions());
        booking.setBookingReference(utilService.generateSaltedString(5));
        return booking;
    }
}
