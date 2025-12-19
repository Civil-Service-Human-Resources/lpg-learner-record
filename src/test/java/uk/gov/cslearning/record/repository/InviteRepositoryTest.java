package uk.gov.cslearning.record.repository;


import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.cslearning.record.IntegrationTestBase;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.domain.Invite;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

@Transactional
public class InviteRepositoryTest extends IntegrationTestBase {

    @Autowired
    private InviteRepository inviteRepository;

    @Test
    public void shouldSaveInvite() {
        Event event = new Event();
        event.setUid("test-catalogue-id");

        Invite invite = new Invite();
        invite.setEvent(event);
        invite.setLearnerEmail("test@test.com");
        invite.setLearnerUid("testUid");

        inviteRepository.save(invite);

        assertThat(invite.getId(), notNullValue());
    }

    @Test
    public void shouldFindInviteById() {
        Event event = new Event();
        event.setUid("test-catalogue-id");

        Invite invite = new Invite();
        invite.setEvent(event);
        invite.setLearnerEmail("test@test.com");
        invite.setLearnerUid("testUid");

        inviteRepository.save(invite);

        Optional<Invite> result = inviteRepository.findById(invite.getId());
        Invite repositoryInvite = result.get();

        assertThat(repositoryInvite, is(equalTo(invite)));

    }

    @Test
    public void shouldContainCorrectEvent() {
        Event event = new Event();
        event.setUid("test-catalogue-id");

        Invite invite = new Invite();
        invite.setEvent(event);
        invite.setLearnerEmail("test@test.com");
        invite.setLearnerUid("testUid");

        inviteRepository.save(invite);

        Optional<Invite> result = inviteRepository.findById(invite.getId());
        Invite repositoryInvite = result.get();

        assertThat(repositoryInvite.getEvent(), is(equalTo(event)));
    }
}
