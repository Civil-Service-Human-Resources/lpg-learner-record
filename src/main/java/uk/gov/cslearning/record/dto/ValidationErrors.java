package uk.gov.cslearning.record.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidationErrors {
    private int size;
    private List<ValidationError> errors;
}
