package uk.gov.cslearning.record.validation.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import uk.gov.cslearning.record.dto.record.CreateLearnerRecordEventDto;
import uk.gov.cslearning.record.validation.annotations.LearnerRecordEventId;

public class LearnerRecordEventIdValidator implements ConstraintValidator<LearnerRecordEventId, CreateLearnerRecordEventDto> {
    @Override
    public boolean isValid(CreateLearnerRecordEventDto dto, ConstraintValidatorContext constraintValidatorContext) {
        return dto.getLearnerRecordId() != null || (dto.getLearnerId() != null && dto.getResourceId() != null);
    }
}
