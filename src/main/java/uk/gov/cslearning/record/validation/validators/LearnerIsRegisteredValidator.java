package uk.gov.cslearning.record.validation.validators;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.service.identity.IdentityService;
import uk.gov.cslearning.record.validation.annotations.LearnerIsRegistered;
import uk.gov.cslearning.record.validation.annotations.LearnerNotInvited;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class LearnerIsRegisteredValidator implements ConstraintValidator<LearnerIsRegistered, String> {

    private final IdentityService identityService;

    public LearnerIsRegisteredValidator(IdentityService identityService) {
        this.identityService = identityService;
    }

    public void initialise(LearnerIsRegistered constraint) {}

    @Override
    public boolean isValid(String learnerEmail, ConstraintValidatorContext context) {
        return identityService.getIdentityByEmailAddress(learnerEmail).isPresent();
    }
}
