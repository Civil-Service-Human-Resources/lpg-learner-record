package uk.gov.cslearning.record.service.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class BlankBearerTokenExceptionHandler {

    @ExceptionHandler(BlankBearerTokenException.class)
    ResponseEntity<ErrorResponse> handleBlankBearerTokenException(BlankBearerTokenException blankBearerTokenException) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(blankBearerTokenException.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
}
