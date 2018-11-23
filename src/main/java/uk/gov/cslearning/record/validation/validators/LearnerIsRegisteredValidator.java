package uk.gov.cslearning.record.validation.validators;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.dto.InviteDto;
import uk.gov.cslearning.record.service.identity.Identity;
import uk.gov.cslearning.record.service.identity.IdentityService;
import uk.gov.cslearning.record.validation.annotations.LearnerNotInvited;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class LearnerIsRegisteredValidator implements ConstraintValidator<LearnerNotInvited, String> {

    private final IdentityService identityService;

    public LearnerIsRegisteredValidator(IdentityService identityService) {
        this.identityService = identityService;
    }

    public void initialise(LearnerNotInvited constraint) {}

    public boolean isValid(String learnerEmail, ConstraintValidatorContext context) {
        Identity identity = identityService.getIdentityByEmailAddress(learnerEmail);

        if(identity == null){
            return false;
        } else {
            return true;
        }
    }
}
