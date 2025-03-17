package uk.gov.cslearning.record.validation.validators;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import uk.gov.cslearning.record.domain.BookingStatus;
import uk.gov.cslearning.record.validation.annotations.BookingStatusNotEquals;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BookingStatusNotEqualsValidatorTest {

    private BookingStatusNotEqualsValidator validator = new BookingStatusNotEqualsValidator();

    @Test
    public void shouldReturnTrueIfValueNotEqualsAnnotationValue() {
        BookingStatusNotEquals annotation = mock(BookingStatusNotEquals.class);
        when(annotation.value()).thenReturn(BookingStatus.REQUESTED);

        validator.initialize(annotation);

        assertTrue(validator.isValid(BookingStatus.CONFIRMED, mock(ConstraintValidatorContext.class)));
    }

    @Test
    public void shouldReturnFalseIfValueDoesNotEqualAnnotationValue() {
        BookingStatusNotEquals annotation = mock(BookingStatusNotEquals.class);
        when(annotation.value()).thenReturn(BookingStatus.REQUESTED);

        validator.initialize(annotation);

        assertFalse(validator.isValid(BookingStatus.REQUESTED, mock(ConstraintValidatorContext.class)));
    }
}
