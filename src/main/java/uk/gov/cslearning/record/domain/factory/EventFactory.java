package uk.gov.cslearning.record.domain.factory;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.domain.Event;

import java.nio.file.Paths;

@Component
public class EventFactory {

    public Event create(String path) {
        Event event = new Event();
        event.setPath(path);
        event.setCatalogueId(Paths.get(path).getFileName().toString());
        return event;
    }
}
