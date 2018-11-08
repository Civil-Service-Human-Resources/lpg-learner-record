package uk.gov.cslearning.record.dto.factory;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import uk.gov.cslearning.record.dto.ErrorDto;
import uk.gov.cslearning.record.dto.ValidationError;
import uk.gov.cslearning.record.dto.ValidationErrors;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ErrorDtoFactoryTest {

    private final ErrorDtoFactory factory = new ErrorDtoFactory();

    @Test
    public void shouldReturnValidationErrors() {
        String error1 = "error-1";
        String error2 = "error-2";

        ErrorDto errorDto = factory.create(HttpStatus.BAD_REQUEST, Arrays.asList(error1, error2));

        assertEquals(400, errorDto.getStatus());
        assertEquals("Bad Request", errorDto.getMessage());
        assertEquals(Arrays.asList(error1, error2), errorDto.getErrors());
    }
}