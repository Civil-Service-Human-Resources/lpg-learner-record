package uk.gov.cslearning.record.dto.factory;


import org.junit.Assert;
import org.junit.Test;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.domain.Invite;
import uk.gov.cslearning.record.dto.InviteDto;

public class InviteDtoFactoryTest {
    private final String learningCatalogueBaseUrl = "http://learning-catalogue/";

    private InviteDtoFactory inviteDtoFactory = new InviteDtoFactory(learningCatalogueBaseUrl);

    @Test
    public void shouldCreateInviteDtoFromInvite(){
        String path = "test/path/abc";

        Event event = new Event();
        event.setPath(path);

        Invite invite = new Invite();
        invite.setEvent(event);
        invite.setId(99);
        invite.setLearnerEmail("test@test.com");

        InviteDto inviteDto = inviteDtoFactory.create(invite);

        Assert.assertEquals(inviteDto.getId(), invite.getId());
        Assert.assertEquals(inviteDto.getEvent().toString(), learningCatalogueBaseUrl + invite.getEvent().getPath());
        Assert.assertEquals(inviteDto.getLearnerEmail(), invite.getLearnerEmail());
    }
}
