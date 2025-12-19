package uk.gov.cslearning.record.dto.factory;


import org.junit.jupiter.api.Test;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.dto.CancellationReason;
import uk.gov.cslearning.record.dto.EventDto;
import uk.gov.cslearning.record.dto.EventStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EventDtoFactoryTest {
    private EventDtoFactory eventDtoFactory = new EventDtoFactory();

    @Test
    public void shouldReturnEventDto() {
        EventStatus status = EventStatus.ACTIVE;
        String uid = "eventUid-uid";

        Event event = new Event();
        event.setStatus(status);
        event.setUid(uid);
        event.setCancellationReason(CancellationReason.UNAVAILABLE);

        EventDto eventDto = eventDtoFactory.create(event);

        assertEquals(status, eventDto.getStatus());
        assertEquals(uid, eventDto.getUid());
        assertEquals(CancellationReason.UNAVAILABLE, eventDto.getCancellationReason());
    }
}
