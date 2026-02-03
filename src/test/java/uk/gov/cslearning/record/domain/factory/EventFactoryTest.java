package uk.gov.cslearning.record.domain.factory;


import org.junit.jupiter.api.Test;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.dto.EventDto;
import uk.gov.cslearning.record.dto.EventStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EventFactoryTest {
    private EventFactory eventFactory = new EventFactory();

    @Test
    public void shouldReturnEventFromUid() {
        String eventUid = "KbOAp+vGQYq7Fu3V6VNAvw";
        Event event = eventFactory.create(eventUid);
        assertEquals(eventUid, event.getUid());
    }

    @Test
    public void shouldReturnEventFromEventDto() {
        EventDto eventDto = new EventDto();

        eventDto.setStatus(EventStatus.ACTIVE);
        String uid = "eventUid";
        eventDto.setUid(uid);

        Event event = eventFactory.create(eventDto);

        assertEquals(EventStatus.ACTIVE, event.getStatus());
        assertEquals(uid, event.getUid());
    }
}
