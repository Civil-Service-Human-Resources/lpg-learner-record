package uk.gov.cslearning.record.domain.factory;

import org.hashids.Hashids;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.domain.Booking;
import uk.gov.cslearning.record.dto.BookingDto;

@Component
public class BookingFactory {

    /**
     * List of allowed characters that can be used in a booking reference.
     * <p>
     * Removing numbers 0 and 1,
     * as well as letters O, I and L to prevent confusion when printed in different fonts.
     */
    private static final String ALLOWED_CHARACTERS = "23456789ABCDEFGHJKMNPQRSTUVWXYZ";

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

        if (bookingDto.getBookingReference() != null) {
            booking.setBookingReference(bookingDto.getBookingReference());
        } else {
            booking.setBookingReference(generateBookingReference());
        }

        if (bookingDto.getCancellationReason() != null) {
            booking.setCancellationReason(bookingDto.getCancellationReason());
        }


        return booking;
    }

    /**
     * Generates a new, unique 5 char booking reference value.
     * Using System.currentTimeMillis() + "" to convert current time to string
     * and use as a salt to ensure the generated value is unique.
     *
     * @return encrypted 5 char code
     */
    private String generateBookingReference() {
        String salt = System.currentTimeMillis() + "";

        Hashids hashids = new Hashids(salt, 5, ALLOWED_CHARACTERS);

        return hashids.encode(1L);
    }
}
