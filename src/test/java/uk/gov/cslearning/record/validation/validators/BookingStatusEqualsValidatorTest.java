package uk.gov.cslearning.record.validation.validators;

import org.junit.Test;
import uk.gov.cslearning.record.dto.BookingStatus;
import uk.gov.cslearning.record.validation.annotations.BookingStatusEquals;

import javax.validation.ConstraintValidatorContext;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BookingStatusEqualsValidatorTest {

    private BookingStatusEqualsValidator validator = new BookingStatusEqualsValidator();

    @Test
    public void shouldReturnTrueIfValueEqualsAnnotationValue() {
        BookingStatusEquals annotation = mock(BookingStatusEquals.class);
        when(annotation.value()).thenReturn(BookingStatus.CONFIRMED);

        validator.initialize(annotation);

        assertTrue(validator.isValid(BookingStatus.CONFIRMED, mock(ConstraintValidatorContext.class)));
    }

    @Test
    public void shouldReturnFalseIfValueDoesNotEqualAnnotationValue() {
        BookingStatusEquals annotation = mock(BookingStatusEquals.class);
        when(annotation.value()).thenReturn(BookingStatus.CONFIRMED);

        validator.initialize(annotation);

        assertFalse(validator.isValid(BookingStatus.REQUESTED, mock(ConstraintValidatorContext.class)));
    }
}