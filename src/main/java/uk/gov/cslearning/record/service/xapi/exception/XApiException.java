package uk.gov.cslearning.record.service.xapi.exception;

public class XApiException extends RuntimeException {
    public XApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
