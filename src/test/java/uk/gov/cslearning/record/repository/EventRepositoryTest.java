package uk.gov.cslearning.record.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.cslearning.record.domain.Event;

import javax.transaction.Transactional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
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
        Event event = new Event();
        event.setPath("test/path");
        event.setCatalogueId("SSE");
        eventRepository.save(event);

        assertThat(event.getId(), notNullValue());
    }

    @Test
    public void shouldBeAbleToFindEventByCatalogueId(){
        Event event = new Event();
        event.setPath("test/path");
        event.setCatalogueId("SBATFEBC");

        eventRepository.save(event);

        Iterable<Event> result = eventRepository.findByCatalogueId("SBATFEBC");

        for(Event e : result){
            assertThat(e, is(equalTo(event)));
        }
    }
}
