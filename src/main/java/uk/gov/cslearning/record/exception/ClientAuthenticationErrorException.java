package uk.gov.cslearning.record.exception;

public class ClientAuthenticationErrorException extends RuntimeException {
    public ClientAuthenticationErrorException(String message) {
        super(message);
    }
}
