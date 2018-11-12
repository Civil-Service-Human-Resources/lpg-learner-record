package uk.gov.cslearning.record.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.cslearning.record.dto.EventDto;
import uk.gov.cslearning.record.dto.EventStatusDto;
import uk.gov.cslearning.record.service.EventService;

@RestController
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PatchMapping(path = "/event/{eventUid}")
    public ResponseEntity cancelEvent(@PathVariable String eventUid, @RequestBody EventStatusDto eventStatus) {
        EventDto event = eventService.updateStatus(eventUid, eventStatus);
        return ResponseEntity.ok(event);
    }
}
