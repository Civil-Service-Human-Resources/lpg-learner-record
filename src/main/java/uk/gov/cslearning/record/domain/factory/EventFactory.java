package uk.gov.cslearning.record.domain.factory;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.dto.EventDto;
import uk.gov.cslearning.record.dto.EventStatus;

@Component
public class EventFactory {

    public Event create(String uid) {
        Event event = new Event();
        event.setUid(uid);
        event.setStatus(EventStatus.ACTIVE);
        return event;
    }

    public Event create(EventDto eventDto) {
        Event event = new Event();
        event.setId(eventDto.getId());
        event.setUid(eventDto.getUid());
        event.setStatus(eventDto.getStatus());
        event.setCancellationReason(eventDto.getCancellationReason());
        return event;
    }

}
