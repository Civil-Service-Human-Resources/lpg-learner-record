package uk.gov.cslearning.record.api;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import uk.gov.cslearning.record.api.output.error.GenericErrorResponse;
import uk.gov.cslearning.record.api.output.error.GenericErrorResponseFactory;
import uk.gov.cslearning.record.dto.error.ErrorDto;
import uk.gov.cslearning.record.dto.error.FieldErrorDto;
import uk.gov.cslearning.record.dto.factory.ErrorDtoFactory;
import uk.gov.cslearning.record.exception.BookingNotFoundException;
import uk.gov.cslearning.record.exception.EventNotFoundException;
import uk.gov.cslearning.record.exception.IncorrectStateException;
import uk.gov.cslearning.record.exception.ModuleRecordNotFoundException;
import uk.gov.cslearning.record.exception.ResourceExists.ResourceExistsException;

import java.util.Collections;


@ControllerAdvice
@Slf4j
public class ApiExceptionHandler {
    private final ErrorDtoFactory errorDtoFactory;
    private final GenericErrorResponseFactory genericErrorResponseFactory;

    public ApiExceptionHandler(ErrorDtoFactory errorDtoFactory, GenericErrorResponseFactory genericErrorResponseFactory) {
        this.errorDtoFactory = errorDtoFactory;
        this.genericErrorResponseFactory = genericErrorResponseFactory;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorDto<FieldErrorDto>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("Bad Request: ", e);

        return ResponseEntity.badRequest().body(errorDtoFactory.createWithErrors(HttpStatus.BAD_REQUEST, e.getFieldErrors()));
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({BookingNotFoundException.class,
            EventNotFoundException.class,
            ModuleRecordNotFoundException.class})
    protected ResponseEntity handleNotFoundException(RuntimeException e) {
        log.error("Not Found: ", e);
        GenericErrorResponse response = new GenericErrorResponse(404, "", Collections.singletonList(e.getMessage()));
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ResourceExistsException.class})
    protected ResponseEntity<GenericErrorResponse> handleResourceExistsException(ResourceExistsException e) {
        GenericErrorResponse response = genericErrorResponseFactory.createResourceExistsException(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<ErrorDto<String>> handleConstraintViolationException(ConstraintViolationException e) {
        log.error("Bad Request: ", e);
        ErrorDto<String> error = errorDtoFactory.create(HttpStatus.BAD_REQUEST, Collections.singletonList("Storage error"));

        return ResponseEntity.badRequest().body(error);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({IncorrectStateException.class})
    protected ResponseEntity<ErrorDto<String>> handleIncorrectStateException(IncorrectStateException e) {
        return ResponseEntity.badRequest().body(errorDtoFactory.create(HttpStatus.BAD_REQUEST, Collections.singletonList(e.getMessage())));
    }
}
