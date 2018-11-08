package uk.gov.cslearning.record.validation.validators;

import uk.gov.cslearning.record.dto.BookingStatus;
import uk.gov.cslearning.record.validation.annotations.BookingStatusEquals;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class BookingStatusEqualsValidator implements ConstraintValidator<BookingStatusEquals, BookingStatus> {
    private BookingStatus annotationValue;

    @Override
    public void initialize(BookingStatusEquals annotation) {
        this.annotationValue = annotation.value();
    }

    @Override
    public boolean isValid(BookingStatus value, ConstraintValidatorContext context) {
        return value.equals(annotationValue);
    }
}
