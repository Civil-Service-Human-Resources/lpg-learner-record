package uk.gov.cslearning.record.validation.validators;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import uk.gov.cslearning.record.dto.EventStatus;
import uk.gov.cslearning.record.validation.annotations.EventStatusNotEquals;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
