package uk.gov.cslearning.record.validation.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.dto.InviteDto;
import uk.gov.cslearning.record.service.InviteService;
import uk.gov.cslearning.record.validation.annotations.LearnerNotInvited;

import java.nio.file.Paths;

@Component
public class LearnerNotInvitedValidator implements ConstraintValidator<LearnerNotInvited, InviteDto> {

    private final InviteService inviteService;

    public LearnerNotInvitedValidator(InviteService inviteService) {
        this.inviteService = inviteService;
    }

    public void initialise(LearnerNotInvited constraint) {
    }

    @Override
    public boolean isValid(InviteDto invite, ConstraintValidatorContext context) {
        String eventUid = Paths.get(invite.getEvent().getPath()).getFileName().toString();

        return !inviteService.findByEventIdAndLearnerEmail(eventUid, invite.getLearnerEmail()).isPresent();
    }
}
