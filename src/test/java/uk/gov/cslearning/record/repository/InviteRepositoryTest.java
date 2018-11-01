package uk.gov.cslearning.record.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.cslearning.record.domain.Invite;

import javax.transaction.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.notNullValue;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class InviteRepositoryTest {

    @Autowired
    private InviteRepository inviteRepository;

    @Test
    public void shouldSaveInvite() {
        Invite invite = new Invite();
        invite.setEvent(new Event());
        invite.setLearnerEmail("test@test.com");

        inviteRepository.save(invite);

        assertThat(invite.getId(), notNullValue());
    }
}
