package uk.gov.cslearning.record.exception;

import lombok.Getter;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@Getter
public class PatchResourceException extends RuntimeException {

    private final List<String> messages = new ArrayList<String>();

    public PatchResourceException(String msg) {
        super(String.format("Failed to apply PATCH to resource. Error: %s", msg));
        messages.add(msg);
    }

    public PatchResourceException(ConstraintViolationException e) {
        super("Failed to parse JSON to object.");
        e.getConstraintViolations().forEach(violation -> messages.add(violationToString(violation)));
    }

    private String violationToString(ConstraintViolation<?> violation) {
        return String.format("Value \"%s\" is invalid for field \"%s\". Error: %s", violation.getInvalidValue(), violation.getPropertyPath(), violation.getMessage());
    }
}
