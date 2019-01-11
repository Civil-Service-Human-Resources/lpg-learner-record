package uk.gov.cslearning.record.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.domain.Invite;

import javax.transaction.Transactional;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class InviteRepositoryTest {

    @Autowired
    private InviteRepository inviteRepository;

    @Test
    public void shouldSaveInvite() {
        Event event = new Event();
        event.setPath("test/path");
        event.setUid("test-catalogue-id");

        Invite invite = new Invite();
        invite.setEvent(event);
        invite.setLearnerEmail("test@test.com");

        inviteRepository.save(invite);

        assertThat(invite.getId(), notNullValue());
    }

    @Test
    public void shouldFindInviteById() {
        Event event = new Event();
        event.setPath("test/path");
        event.setUid("test-catalogue-id");

        Invite invite = new Invite();
        invite.setEvent(event);
        invite.setLearnerEmail("test@test.com");

        inviteRepository.save(invite);

        Optional<Invite> result = inviteRepository.findById(invite.getId());
        Invite repositoryInvite = result.get();

        assertThat(repositoryInvite, is(equalTo(invite)));

    }

    @Test
    public void shouldContainCorrectEvent(){
        Event event = new Event();
        event.setPath("test/path");
        event.setUid("test-catalogue-id");

        Invite invite = new Invite();
        invite.setEvent(event);
        invite.setLearnerEmail("test@test.com");

        inviteRepository.save(invite);

        Optional<Invite> result = inviteRepository.findById(invite.getId());
        Invite repositoryInvite = result.get();

        assertThat(repositoryInvite.getEvent(), is(equalTo(event)));
    }
}
