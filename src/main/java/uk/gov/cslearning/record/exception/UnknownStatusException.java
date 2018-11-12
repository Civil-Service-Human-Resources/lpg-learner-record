package uk.gov.cslearning.record.exception;

public class UnknownStatusException extends RuntimeException {
    public UnknownStatusException(String value) {
        super(value);
    }
}
