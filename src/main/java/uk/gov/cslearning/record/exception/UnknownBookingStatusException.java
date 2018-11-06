package uk.gov.cslearning.record.exception;

public class UnknownBookingStatusException extends RuntimeException {
    public UnknownBookingStatusException(String value) {
        super(value);
    }
}
