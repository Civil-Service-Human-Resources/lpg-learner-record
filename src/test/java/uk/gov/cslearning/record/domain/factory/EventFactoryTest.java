package uk.gov.cslearning.record.domain.factory;


import org.junit.jupiter.api.Test;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.dto.EventDto;
import uk.gov.cslearning.record.dto.EventStatus;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EventFactoryTest {
    private EventFactory eventFactory = new EventFactory();

    @Test
    public void shouldReturnEventFromPath() {
        String eventUid = "KbOAp+vGQYq7Fu3V6VNAvw";
        String eventPath = "/courses/Vy836d5IT+mgyVQ4dAVrHQ/modules/ZHsOVpU6Sgunlc/zVMbpJw/events/" + eventUid;

        Event event = eventFactory.create(eventPath);

        assertEquals(eventPath, event.getPath());
        assertEquals(eventUid, event.getUid());
    }

    @Test
    public void shouldReturnEventFromEventDto() {
        URI uri = URI.create("http://example.org/path/to/event");

        EventDto eventDto = new EventDto();

        eventDto.setStatus(EventStatus.ACTIVE);
        eventDto.setUri(uri);
        String uid = "event";
        eventDto.setUid(uid);

        Event event = eventFactory.create(eventDto);

        assertEquals(EventStatus.ACTIVE, event.getStatus());
        assertEquals(uid, event.getUid());
        assertEquals(uri.getPath(), event.getPath());
    }
}
