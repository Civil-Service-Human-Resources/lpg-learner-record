package uk.gov.cslearning.record.exception;

public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(int bookingId) {
        super(String.format("Booking does not exist with id: %d", bookingId));
    }

    public BookingNotFoundException(String eventUid, String bookingUid) {
        super(String.format("Booking does not exist with eventUid: %s and learner: %s", eventUid, bookingUid));
    }
}
