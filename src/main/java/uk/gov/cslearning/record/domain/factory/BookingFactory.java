package uk.gov.cslearning.record.domain.factory;

import org.hashids.Hashids;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.domain.Booking;
import uk.gov.cslearning.record.dto.BookingDto;

@Component
public class BookingFactory {
    private static final String ALLOWED_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
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
        booking.setBookingReference(generateBookingReference(bookingDto.getPoNumber()));
        booking.setAccessibilityOptions(bookingDto.getAccessibilityOptions());

        if (bookingDto.getCancellationReason() != null) {
            booking.setCancellationReason(bookingDto.getCancellationReason());
        }

        return booking;
    }

        public String generateBookingReference(String poNumber) {
        Hashids hashids = new Hashids(System.currentTimeMillis() + poNumber, 5, ALLOWED_CHARACTERS);
        return hashids.encode(1L);
    }
}
