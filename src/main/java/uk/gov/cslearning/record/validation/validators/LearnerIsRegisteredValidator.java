package uk.gov.cslearning.record.validation.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.service.identity.IdentitiesService;
import uk.gov.cslearning.record.validation.annotations.LearnerIsRegistered;

@Component
public class LearnerIsRegisteredValidator implements ConstraintValidator<LearnerIsRegistered, String> {

    private final IdentitiesService identityService;

    public LearnerIsRegisteredValidator(IdentitiesService identityService) {
        this.identityService = identityService;
    }

    public void initialise(LearnerIsRegistered constraint) {
    }

    @Override
    public boolean isValid(String learnerEmail, ConstraintValidatorContext context) {
        return identityService.getIdentityByEmailAddress(learnerEmail).isPresent();
    }
}
