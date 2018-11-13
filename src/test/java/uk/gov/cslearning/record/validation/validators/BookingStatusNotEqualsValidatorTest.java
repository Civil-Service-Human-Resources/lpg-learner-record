package uk.gov.cslearning.record.validation.validators;

import org.junit.Test;
import uk.gov.cslearning.record.dto.BookingStatus;
import uk.gov.cslearning.record.validation.annotations.BookingStatusNotEquals;

import javax.validation.ConstraintValidatorContext;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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