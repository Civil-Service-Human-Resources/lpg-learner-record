package uk.gov.cslearning.record.api;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class ApiExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

    private final ErrorDtoFactory errorDtoFactory;
    private final GenericErrorResponseFactory genericErrorResponseFactory;

    public ApiExceptionHandler(ErrorDtoFactory errorDtoFactory, GenericErrorResponseFactory genericErrorResponseFactory) {
        this.errorDtoFactory = errorDtoFactory;
        this.genericErrorResponseFactory = genericErrorResponseFactory;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        LOGGER.error("Bad Request: ", e);

        List<String> errors = e.getBindingResult().getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.toList());

        return ResponseEntity.badRequest().body(errorDtoFactory.create(HttpStatus.BAD_REQUEST, errors));
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({BookingNotFoundException.class,
            EventNotFoundException.class,
            CourseRecordNotFoundException.class,
            ModuleRecordNotFoundException.class})
    protected ResponseEntity handleNotFoundException(RuntimeException e) {
        LOGGER.error("Not Found: ", e);
        GenericErrorResponse response = new GenericErrorResponse(404, "", Collections.singletonList(e.getMessage()));
        return new ResponseEntity<GenericErrorResponse>(response, HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ResourceExistsException.class})
    protected ResponseEntity<GenericErrorResponse> handleResourceExistsException(ResourceExistsException e) {
        GenericErrorResponse response = genericErrorResponseFactory.createResourceExistsException(e.getMessage());
        return new ResponseEntity<GenericErrorResponse>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<ErrorDto> handleConstraintViolationException(ConstraintViolationException e) {
        LOGGER.error("Bad Request: ", e);
        ErrorDto error = errorDtoFactory.create(HttpStatus.BAD_REQUEST, Collections.singletonList("Storage error"));

        return ResponseEntity.badRequest().body(error);
    }
}
