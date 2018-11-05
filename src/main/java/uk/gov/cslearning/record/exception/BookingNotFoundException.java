package uk.gov.cslearning.record.exception;

public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(int bookingId) {
        super(String.format("Booking does not exist with id: %d", bookingId));
    }
}
