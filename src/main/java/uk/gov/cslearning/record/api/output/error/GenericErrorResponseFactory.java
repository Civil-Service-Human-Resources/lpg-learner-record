package uk.gov.cslearning.record.api.output.error;

import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class GenericErrorResponseFactory {

    public GenericErrorResponse createResourceExistsException(String message) {
        return new GenericErrorResponse(400, "", Collections.singletonList(message));
    }
}
