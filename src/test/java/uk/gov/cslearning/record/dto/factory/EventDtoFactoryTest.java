package uk.gov.cslearning.record.dto.factory;

import org.junit.Test;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.dto.CancellationReason;
import uk.gov.cslearning.record.dto.EventDto;
import uk.gov.cslearning.record.dto.EventStatus;

import java.net.URI;

import static org.junit.Assert.*;

public class EventDtoFactoryTest {
    private static final String catalogueUrl = "http://example.org";

    private EventDtoFactory eventDtoFactory = new EventDtoFactory(catalogueUrl);

    @Test
    public void shouldReturnEventDto() {
        EventStatus status = EventStatus.ACTIVE;
        String path = "/path/to/event";
        String uid = "event-uid";

        Event event = new Event();
        event.setStatus(status);
        event.setPath(path);
        event.setUid(uid);
        event.setCancellationReason(CancellationReason.UNAVAILABLE);

        EventDto eventDto = eventDtoFactory.create(event);

        assertEquals(status, eventDto.getStatus());
        assertEquals(URI.create(String.join("", catalogueUrl, path)), eventDto.getUri());
        assertEquals(uid, eventDto.getUid());
        assertEquals(CancellationReason.UNAVAILABLE, eventDto.getCancellationReason());
    }
}