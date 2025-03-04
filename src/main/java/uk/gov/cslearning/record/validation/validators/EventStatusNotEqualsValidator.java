package uk.gov.cslearning.record.validation.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import uk.gov.cslearning.record.dto.EventStatus;
import uk.gov.cslearning.record.validation.annotations.EventStatusNotEquals;

public class EventStatusNotEqualsValidator implements ConstraintValidator<EventStatusNotEquals, EventStatus> {
    private EventStatus annotationValue;

    public void initialize(EventStatusNotEquals annotation) {
        this.annotationValue = annotation.value();
    }

    public boolean isValid(EventStatus value, ConstraintValidatorContext context) {
        return !value.equals(annotationValue);
    }
}
