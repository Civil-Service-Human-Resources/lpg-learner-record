package uk.gov.cslearning.record.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.cslearning.record.domain.Event;

import javax.transaction.Transactional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @Test
    public void shouldSaveEvent(){
        Event event = new Event(new Long(1));
        event.setPath("test/path");
        eventRepository.save(event);

        assertThat(event.getId(), notNullValue());
    }
}
