package uk.gov.cslearning.record.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import uk.gov.cslearning.record.dto.ValidationErrors;
import uk.gov.cslearning.record.dto.factory.ValidationErrorsFactory;

@ControllerAdvice
public class ApiExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

    private final ValidationErrorsFactory validationErrorsFactory;

    public ApiExceptionHandler(ValidationErrorsFactory validationErrorsFactory) {
        this.validationErrorsFactory = validationErrorsFactory;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ValidationErrors> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        LOGGER.error("Bad Request: ", e);

        return ResponseEntity.badRequest().body(validationErrorsFactory.create(e.getBindingResult().getFieldErrors()));
    }
}