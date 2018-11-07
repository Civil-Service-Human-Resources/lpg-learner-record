package uk.gov.cslearning.record.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @DeleteMapping(path = "/event/{eventId}")
    public ResponseEntity cancelEvent(@PathVariable String eventId) {
        eventService.cancelEvent(eventId);
        return ResponseEntity.noContent().build();
    }
}
