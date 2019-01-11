package uk.gov.cslearning.record.validation.validators;

import org.junit.Test;
import uk.gov.cslearning.record.dto.EventStatus;
import uk.gov.cslearning.record.validation.annotations.EventStatusNotEquals;

import javax.validation.ConstraintValidatorContext;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EventStatusNotEqualsValidatorTest {

    private EventStatusNotEqualsValidator validator = new EventStatusNotEqualsValidator();

    @Test
    public void shouldReturnTrueIfValueNotEqualsAnnotationValue() {
        EventStatusNotEquals annotation = mock(EventStatusNotEquals.class);
        when(annotation.value()).thenReturn(EventStatus.ACTIVE);

        validator.initialize(annotation);

        assertTrue(validator.isValid(EventStatus.CANCELLED, mock(ConstraintValidatorContext.class)));
    }

    @Test
    public void shouldReturnFalseIfValueDoesNotEqualAnnotationValue() {
        EventStatusNotEquals annotation = mock(EventStatusNotEquals.class);
        when(annotation.value()).thenReturn(EventStatus.ACTIVE);

        validator.initialize(annotation);

        assertFalse(validator.isValid(EventStatus.ACTIVE, mock(ConstraintValidatorContext.class)));
    }
}