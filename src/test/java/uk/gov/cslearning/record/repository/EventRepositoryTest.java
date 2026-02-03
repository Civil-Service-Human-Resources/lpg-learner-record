package uk.gov.cslearning.record.repository;


import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.cslearning.record.IntegrationTestBase;
import uk.gov.cslearning.record.domain.Event;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

@Transactional
public class EventRepositoryTest extends IntegrationTestBase {

    @Autowired
    private EventRepository eventRepository;

    @Test
    public void shouldSaveEvent() {
        Event event = new Event();
        event.setUid("SSE");
        eventRepository.save(event);

        assertThat(event.getId(), notNullValue());
    }

    @Test
    public void shouldBeAbleToFindEventByUid() {
        Event event = new Event();
        event.setUid("SBATFEBC");

        eventRepository.save(event);

        Event repositoryEvent = eventRepository.findByUid("SBATFEBC").get();

        assertThat(repositoryEvent, is(equalTo(event)));
    }
}
