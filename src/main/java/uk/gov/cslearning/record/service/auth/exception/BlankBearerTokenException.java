package uk.gov.cslearning.record.service.auth.exception;

public class BlankBearerTokenException extends RuntimeException {
    public BlankBearerTokenException(String message) {
        super(message);
    }
}
