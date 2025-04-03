package uk.gov.cslearning.record.dto.error;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.List;


@Data
@AllArgsConstructor
public class ErrorDto<T> {
    private final Instant timestamp = Instant.now();
    private List<T> errors;
    private int status;
    private String message;
}
