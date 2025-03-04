package uk.gov.cslearning.record.api;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import uk.gov.cslearning.record.api.output.error.GenericErrorResponse;
import uk.gov.cslearning.record.api.output.error.GenericErrorResponseFactory;
import uk.gov.cslearning.record.dto.ErrorDto;
import uk.gov.cslearning.record.dto.factory.ErrorDtoFactory;
import uk.gov.cslearning.record.exception.BookingNotFoundException;
import uk.gov.cslearning.record.exception.CourseRecordNotFoundException;
import uk.gov.cslearning.record.exception.EventNotFoundException;
import uk.gov.cslearning.record.exception.ModuleRecordNotFoundException;
import uk.gov.cslearning.record.exception.ResourceExists.ResourceExistsException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


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
    protected ResponseEntity<ErrorDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("Bad Request: ", e);

        List<String> errors = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        return ResponseEntity.badRequest().body(errorDtoFactory.create(HttpStatus.BAD_REQUEST, errors));
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({BookingNotFoundException.class,
            EventNotFoundException.class,
            CourseRecordNotFoundException.class,
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
    protected ResponseEntity<ErrorDto> handleConstraintViolationException(ConstraintViolationException e) {
        log.error("Bad Request: ", e);
        ErrorDto error = errorDtoFactory.create(HttpStatus.BAD_REQUEST, Collections.singletonList("Storage error"));

        return ResponseEntity.badRequest().body(error);
    }
}
