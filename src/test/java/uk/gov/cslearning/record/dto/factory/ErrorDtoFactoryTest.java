package uk.gov.cslearning.record.dto.factory;


import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import uk.gov.cslearning.record.dto.error.ErrorDto;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ErrorDtoFactoryTest {

    private final ErrorDtoFactory factory = new ErrorDtoFactory();

    @Test
    public void shouldReturnValidationErrors() {
        String error1 = "error-1";
        String error2 = "error-2";

        ErrorDto<String> errorDto = factory.create(HttpStatus.BAD_REQUEST, Arrays.asList(error1, error2));

        assertEquals(400, errorDto.getStatus());
        assertEquals("Bad Request", errorDto.getMessage());
        assertEquals(Arrays.asList(error1, error2), errorDto.getErrors());
    }
}
