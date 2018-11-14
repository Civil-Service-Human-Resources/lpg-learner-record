package uk.gov.cslearning.record.dto;

import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@Data
public class ErrorDto {
    private final Instant timestamp = Instant.now();
    private List<String> errors = new ArrayList<>();
    private int status;
    private String message;
}
