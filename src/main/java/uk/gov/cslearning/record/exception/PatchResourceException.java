package uk.gov.cslearning.record.exception;

public class PatchResourceException extends RuntimeException {

    public PatchResourceException(String msg) {
        super(String.format("Failed to apply PATCH to resource: %s", msg));
    }
}
