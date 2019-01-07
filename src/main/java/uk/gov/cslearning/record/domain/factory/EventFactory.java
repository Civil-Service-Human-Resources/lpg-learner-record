package uk.gov.cslearning.record.domain.factory;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.dto.EventDto;
import uk.gov.cslearning.record.dto.EventStatus;

import java.nio.file.Paths;

@Component
public class EventFactory {

    public Event create(String path) {
        Event event = new Event();
        event.setPath(path);
        event.setUid(Paths.get(path).getFileName().toString());
        event.setStatus(EventStatus.ACTIVE);
        return event;
    }

    public Event create(EventDto eventDto) {
        String path = eventDto.getUri().getPath();

        Event event = new Event();
        event.setId(eventDto.getId());
        event.setPath(path);
        event.setUid(Paths.get(path).getFileName().toString());
        event.setStatus(eventDto.getStatus());
        event.setCancellationReason(eventDto.getCancellationReason());
        return event;
    }

}
