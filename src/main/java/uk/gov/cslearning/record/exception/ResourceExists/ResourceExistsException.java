package uk.gov.cslearning.record.exception.ResourceExists;

public class ResourceExistsException extends RuntimeException {

    public ResourceExistsException(String msg) {
        super(String.format("Resource already exists: %s", msg));
    }
}
