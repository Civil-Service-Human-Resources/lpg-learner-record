package uk.gov.cslearning.record.dto.factory;


import org.junit.jupiter.api.Test;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.domain.Invite;
import uk.gov.cslearning.record.dto.InviteDto;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InviteDtoFactoryTest {
    private InviteDtoFactory inviteDtoFactory = new InviteDtoFactory();

    @Test
    public void shouldCreateInviteDtoFromInvite() {

        String uid = "abc";
        Event event = new Event();
        event.setUid(uid);

        Invite invite = new Invite();
        invite.setEvent(event);
        invite.setId(99);
        invite.setLearnerEmail("test@test.com");

        InviteDto inviteDto = inviteDtoFactory.create(invite);

        assertEquals(inviteDto.getId(), invite.getId());
        assertEquals(inviteDto.getLearnerEmail(), invite.getLearnerEmail());
    }
}
