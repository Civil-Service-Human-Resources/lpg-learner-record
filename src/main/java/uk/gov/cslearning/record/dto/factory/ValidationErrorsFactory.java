package uk.gov.cslearning.record.dto.factory;

import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import uk.gov.cslearning.record.dto.ValidationError;
import uk.gov.cslearning.record.dto.ValidationErrors;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ValidationErrorsFactory {
    public ValidationErrors create(List<FieldError> fieldErrors) {
        List<FieldError> errors = new ArrayList<>(fieldErrors);

        errors.sort((o1, o2) -> (o1.getField().compareTo(o2.getField())));

        ValidationErrors validationErrors = new ValidationErrors();
        validationErrors.setSize(errors.size());
        validationErrors.setErrors(errors.stream()
                .map(e -> new ValidationError(e.getField(), e.getDefaultMessage()))
                .collect(Collectors.toList())
        );

        return validationErrors;
    }
}
