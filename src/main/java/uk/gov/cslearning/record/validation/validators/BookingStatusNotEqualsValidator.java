package uk.gov.cslearning.record.validation.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import uk.gov.cslearning.record.domain.BookingStatus;
import uk.gov.cslearning.record.validation.annotations.BookingStatusNotEquals;

public class BookingStatusNotEqualsValidator implements ConstraintValidator<BookingStatusNotEquals, BookingStatus> {
    private BookingStatus annotationValue;

    @Override
    public void initialize(BookingStatusNotEquals annotation) {
        this.annotationValue = annotation.value();
    }

    @Override
    public boolean isValid(BookingStatus value, ConstraintValidatorContext context) {
        return !value.equals(annotationValue);
    }
}
