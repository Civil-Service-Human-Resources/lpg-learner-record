package uk.gov.cslearning.record.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.cslearning.record.domain.Invite;

import javax.transaction.Transactional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.notNullValue;

import uk.gov.cslearning.record.domain.Event;

import java.util.Optional;

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
        event.setCatalogueId("SSI");

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
        event.setCatalogueId("SFIBI");

        Invite invite = new Invite();
        invite.setEvent(event);
        invite.setLearnerEmail("test@test.com");

        inviteRepository.save(invite);

        Optional<Invite> result = inviteRepository.findById(1);
        Invite repositoryInvite = result.get();

        assertThat(repositoryInvite, is(equalTo(invite)));

    }

    @Test
    public void shouldContainCorrectEvent(){
        Event event = new Event();
        event.setPath("test/path");
        event.setCatalogueId("SCCE");

        Invite invite = new Invite();
        invite.setEvent(event);
        invite.setLearnerEmail("test@test.com");

        inviteRepository.save(invite);

        Optional<Invite> result = inviteRepository.findById(invite.getId());
        Invite repositoryInvite = result.get();

        assertThat(repositoryInvite.getEvent(), is(equalTo(event)));
    }
}
