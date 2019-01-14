package uk.gov.cslearning.record.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cslearning.record.dto.CancellationReason;
import uk.gov.cslearning.record.dto.EventDto;
import uk.gov.cslearning.record.dto.EventStatusDto;
import uk.gov.cslearning.record.service.EventService;

import java.util.Arrays;

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

    @PostMapping(path = "/event")
    public ResponseEntity<EventDto> create(@RequestBody EventDto event, UriComponentsBuilder uriBuilder) {
        EventDto result = eventService.create(event);

        return ResponseEntity.created(
                uriBuilder.path("/event/{eventId}").build(result.getUid())
        ).build();
    }

    @GetMapping(path = "/event/cancellationReasons")
    public ResponseEntity<Iterable<CancellationReason>> getCancellationReasons() {
        return new ResponseEntity<>(Arrays.asList(CancellationReason.values()), OK);
    }
}
