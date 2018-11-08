package uk.gov.cslearning.record.dto.factory;

import org.junit.Test;
import org.springframework.validation.FieldError;
import uk.gov.cslearning.record.dto.ValidationError;
import uk.gov.cslearning.record.dto.ValidationErrors;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ValidationErrorsFactoryTest {

    private final ValidationErrorsFactory factory = new ValidationErrorsFactory();

    @Test
    public void shouldReturnValidationErrors() {
        String field1 = "_field1";
        String field2 = "_field2";

        String defaultMessage1 = "message1";
        String defaultMessage2 = "message2";

        FieldError error1 = mock(FieldError.class);
        FieldError error2 = mock(FieldError.class);

        when(error1.getField()).thenReturn(field1);
        when(error1.getDefaultMessage()).thenReturn(defaultMessage1);

        when(error2.getField()).thenReturn(field2);
        when(error2.getDefaultMessage()).thenReturn(defaultMessage2);

        ValidationErrors validationErrors = factory.create(Arrays.asList(error1, error2));

        assertEquals(2, validationErrors.getSize());
        assertEquals(new ValidationError("_field1", "message1"), validationErrors.getErrors().get(0));
        assertEquals(new ValidationError("_field2", "message2"), validationErrors.getErrors().get(1));
    }
}