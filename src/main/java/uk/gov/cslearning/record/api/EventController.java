package uk.gov.cslearning.record.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.cslearning.record.dto.EventDto;
import uk.gov.cslearning.record.dto.EventStatusDto;
import uk.gov.cslearning.record.service.EventService;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@RestController
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PatchMapping(path = "/event/{eventUid}")
    public ResponseEntity cancelEvent(@PathVariable String eventUid, @RequestBody EventStatusDto eventStatus) {
        return eventService.updateStatus(eventUid, eventStatus).
            map(e -> new ResponseEntity(e, OK))
            .orElseGet(() -> new ResponseEntity(NOT_FOUND));
    }

    @GetMapping(path = "/event/{eventUid}")
    public ResponseEntity<EventDto> find(@PathVariable String eventUid) {
        return eventService.findByUid(eventUid)
                .map(b -> new ResponseEntity<>(b, OK))
                .orElseGet(() -> new ResponseEntity<>(NOT_FOUND));
    }

}
