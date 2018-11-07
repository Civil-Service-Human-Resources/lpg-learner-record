package uk.gov.cslearning.record.exception;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(String catalogueId) {
        super(String.format("Event does not exist with catalogue id: %s", catalogueId));
    }
}
