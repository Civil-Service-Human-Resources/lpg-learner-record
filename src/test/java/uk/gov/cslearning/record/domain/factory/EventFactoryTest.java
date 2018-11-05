package uk.gov.cslearning.record.domain.factory;

import org.junit.Test;
import uk.gov.cslearning.record.domain.Event;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class EventFactoryTest {
    private EventFactory eventFactory = new EventFactory();

    @Test
    public void shouldReturnEvent() {
        String eventPath = "event-path";

        Event event = eventFactory.create(eventPath);

        assertThat(event.getPath(), equalTo(eventPath));
    }
}