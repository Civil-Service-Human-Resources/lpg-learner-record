package uk.gov.cslearning.record.dto.factory;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.dto.ErrorDto;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.sort;

@Component
public class ErrorDtoFactory {
    public ErrorDto create(HttpStatus httpStatus, List<String> errors) {
        sort(errors);
        ErrorDto errorDto = new ErrorDto();
        errorDto.setStatus(httpStatus.value());
        errorDto.setMessage(httpStatus.getReasonPhrase());
        errorDto.setErrors(new ArrayList<>(errors));
        return errorDto;
    }
}
