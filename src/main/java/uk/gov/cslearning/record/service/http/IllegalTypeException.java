package uk.gov.cslearning.record.service.http;

public class IllegalTypeException extends RuntimeException {
    public IllegalTypeException(Class type) {
        super(String.format("Unrecognized type: %s", type.getName()));
    }
}
