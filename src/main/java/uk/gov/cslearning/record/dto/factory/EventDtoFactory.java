package uk.gov.cslearning.record.dto.factory;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.dto.EventDto;

@Component
public class EventDtoFactory {

    public EventDto create(Event event) {
        EventDto eventDto = new EventDto();
        eventDto.setId(event.getId());
        eventDto.setStatus(event.getStatus());
        eventDto.setUid(event.getUid());
        eventDto.setCancellationReason(event.getCancellationReason());
        return eventDto;
    }
}
