package uk.gov.cslearning.record.dto.factory;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import uk.gov.cslearning.record.dto.error.ErrorDto;
import uk.gov.cslearning.record.dto.error.FieldErrorDto;

import java.util.Comparator;
import java.util.List;

import static java.util.Collections.sort;

@Component
public class ErrorDtoFactory {
    public ErrorDto<String> create(HttpStatus httpStatus, List<String> errors) {
        sort(errors);
        return new ErrorDto<>(errors, httpStatus.value(), httpStatus.getReasonPhrase());
    }

    public ErrorDto<FieldErrorDto> createWithErrors(HttpStatus httpStatus, List<FieldError> errors) {
        List<FieldErrorDto> errorDtos = errors.stream()
                .map(e -> new FieldErrorDto(e.getField(), e.getDefaultMessage()))
                .sorted(Comparator.comparing(FieldErrorDto::getError)).toList();
        return new ErrorDto<>(errorDtos, httpStatus.value(), httpStatus.getReasonPhrase());
    }
}
