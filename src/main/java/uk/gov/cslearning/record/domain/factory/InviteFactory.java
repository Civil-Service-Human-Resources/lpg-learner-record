package uk.gov.cslearning.record.domain.factory;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.domain.Invite;
import uk.gov.cslearning.record.dto.InviteDto;

@Component
public class InviteFactory {
    private final EventFactory eventFactory;

    public InviteFactory(EventFactory eventFactory) {
        this.eventFactory = eventFactory;
    }

    public Invite create(InviteDto inviteDto, Event event){
        Invite invite = new Invite();
        invite.setId(inviteDto.getId());
        invite.setLearnerEmail(inviteDto.getLearnerEmail());
        invite.setEvent(event);

        return invite;
    }
}
