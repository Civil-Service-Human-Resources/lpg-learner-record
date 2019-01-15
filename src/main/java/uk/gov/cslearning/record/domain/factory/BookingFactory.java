package uk.gov.cslearning.record.domain.factory;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.domain.Booking;
import uk.gov.cslearning.record.dto.BookingDto;

@Component
public class BookingFactory {
    private final EventFactory eventFactory;
    private final LearnerFactory learnerFactory;

    public BookingFactory(EventFactory eventFactory, LearnerFactory learnerFactory) {
        this.eventFactory = eventFactory;
        this.learnerFactory = learnerFactory;
    }

    public Booking create(BookingDto bookingDto) {
        Booking booking = new Booking();

        booking.setBookingTime(bookingDto.getBookingTime());
        booking.setConfirmationTime(bookingDto.getConfirmationTime());
        booking.setCancellationTime(bookingDto.getCancellationTime());
        booking.setEvent(eventFactory.create(bookingDto.getEvent().getPath()));

        if (null != bookingDto.getPaymentDetails()) {
            booking.setPaymentDetails(bookingDto.getPaymentDetails().getPath());
        }

        booking.setLearner(learnerFactory.create(bookingDto.getLearner(), bookingDto.getLearnerEmail()));
        booking.setId(bookingDto.getId());
        booking.setStatus(bookingDto.getStatus());
        booking.setPoNumber(bookingDto.getPoNumber());
        booking.setAccessibilityOptions(bookingDto.getAccessibilityOptions());

        if (bookingDto.getCancellationReason() != null) {
            booking.setCancellationReason(bookingDto.getCancellationReason());
        }

        return booking;
    }
}
