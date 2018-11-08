package uk.gov.cslearning.record.domain.factory;

import org.junit.Test;
import uk.gov.cslearning.record.domain.Event;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class EventFactoryTest {
    private EventFactory eventFactory = new EventFactory();

    @Test
    public void shouldReturnEvent() {
        String catalogueId = "KbOAp+vGQYq7Fu3V6VNAvw";
        String eventPath = "/courses/Vy836d5IT+mgyVQ4dAVrHQ/modules/ZHsOVpU6Sgunlc/zVMbpJw/events/" + catalogueId;

        Event event = eventFactory.create(eventPath);

        assertThat(event.getPath(), equalTo(eventPath));
        assertThat(event.getCatalogueId(), equalTo(catalogueId));
    }
}