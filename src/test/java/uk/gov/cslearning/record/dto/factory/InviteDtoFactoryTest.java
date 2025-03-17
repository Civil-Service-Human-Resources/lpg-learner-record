package uk.gov.cslearning.record.dto.factory;


import org.junit.jupiter.api.Test;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.domain.Invite;
import uk.gov.cslearning.record.dto.InviteDto;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InviteDtoFactoryTest {
    private final String learningCatalogueBaseUrl = "http://learning-catalogue";

    private InviteDtoFactory inviteDtoFactory = new InviteDtoFactory(learningCatalogueBaseUrl);

    @Test
    public void shouldCreateInviteDtoFromInvite() {
        String path = "/test/path/abc";

        Event event = new Event();
        event.setPath(path);

        Invite invite = new Invite();
        invite.setEvent(event);
        invite.setId(99);
        invite.setLearnerEmail("test@test.com");

        InviteDto inviteDto = inviteDtoFactory.create(invite);

        assertEquals(inviteDto.getId(), invite.getId());
        assertEquals("http://learning-catalogue/test/path/abc", inviteDto.getEvent().toString());
        assertEquals(inviteDto.getLearnerEmail(), invite.getLearnerEmail());
    }
}
