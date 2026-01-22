package uk.gov.cslearning.record.dto.factory;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.domain.Invite;
import uk.gov.cslearning.record.dto.InviteDto;

@Component
public class InviteDtoFactory {
    public InviteDto create(Invite invite) {
        InviteDto inviteDto = new InviteDto();
        inviteDto.setId(invite.getId());
        inviteDto.setLearnerEmail(invite.getLearnerEmail());
        inviteDto.setLearnerUid(invite.getLearnerUid());

        return inviteDto;
    }
}
