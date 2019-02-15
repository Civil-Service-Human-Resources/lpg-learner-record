package uk.gov.cslearning.record.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String userId) {
        super(String.format("Unknown user: %s", userId));
    }
}
