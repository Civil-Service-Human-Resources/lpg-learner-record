package uk.gov.cslearning.record.domain.factory;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.domain.Event;

@Component
public class EventFactory {

    public Event create(String path) {
        Event event = new Event();
        event.setPath(path);
        return event;
    }
}
