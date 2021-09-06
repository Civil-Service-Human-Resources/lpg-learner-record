package uk.gov.cslearning.record.api;

import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.Patch;
import com.microsoft.applicationinsights.core.dependencies.google.api.Http;
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
import uk.gov.cslearning.record.dto.ErrorDto;
import uk.gov.cslearning.record.dto.factory.ErrorDtoFactory;
import uk.gov.cslearning.record.exception.BookingNotFoundException;
import uk.gov.cslearning.record.exception.EventNotFoundException;
import uk.gov.cslearning.record.exception.CourseRecordNotFoundException;
import com.github.fge.jsonpatch.JsonPatchException;
import uk.gov.cslearning.record.exception.PatchResourceException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@ControllerAdvice
public class ApiExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

    private final ErrorDtoFactory errorDtoFactory;

    public ApiExceptionHandler(ErrorDtoFactory errorDtoFactory) {
        this.errorDtoFactory = errorDtoFactory;
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
    @ExceptionHandler({BookingNotFoundException.class, EventNotFoundException.class, CourseRecordNotFoundException.class})
    protected ResponseEntity handleNotFoundException(RuntimeException e) {
        LOGGER.error("Not Found: ", e);

        return ResponseEntity.notFound().build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(PatchResourceException.class)
    protected ResponseEntity handleResourcePatchException(PatchResourceException e) {
        GenericErrorResponse responseBody = new GenericErrorResponse(400, "", e);
        return ResponseEntity.badRequest().body(responseBody);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<ErrorDto> handleConstraintViolationException(ConstraintViolationException e) {
        LOGGER.error("Bad Request: ", e);

        ErrorDto error = errorDtoFactory.create(HttpStatus.BAD_REQUEST, Collections.singletonList("Storage error"));

        return ResponseEntity.badRequest().body(error);
    }
}