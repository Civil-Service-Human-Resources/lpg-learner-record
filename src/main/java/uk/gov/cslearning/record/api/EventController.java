package uk.gov.cslearning.record.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cslearning.record.dto.CancellationReason;
import uk.gov.cslearning.record.dto.EventDto;
import uk.gov.cslearning.record.dto.EventStatusDto;
import uk.gov.cslearning.record.service.EventService;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@RestController
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping(path = "/events-list")
    public ResponseEntity<List<EventDto>> getEvents(@RequestParam(value = "uids") List<String> eventUids,
                                                    @RequestParam(value = "getBookingCount", defaultValue = "false") boolean getBookingCount) {
        List<EventDto> events = eventService.getEvents(eventUids, getBookingCount);
        return new ResponseEntity<>(events, OK);
    }

    @PatchMapping(path = "/event/{eventUid}")
    public ResponseEntity<EventDto> cancelEvent(@PathVariable String eventUid, @RequestBody EventStatusDto eventStatus) {
        return new ResponseEntity<>(eventService.updateStatus(eventUid, eventStatus), OK);
    }

    @GetMapping(path = "/event/{eventUid}")
    public ResponseEntity<EventDto> find(@PathVariable String eventUid,
                                         @RequestParam(value = "getBookingCount", defaultValue = "false") boolean getBookingCount) {
        EventDto event = eventService.findByUid(eventUid, getBookingCount);
        if (event == null) {
            return new ResponseEntity<>(NOT_FOUND);
        } else {
            return new ResponseEntity<>(event, OK);
        }
    }

    @PostMapping(path = "/event")
    public ResponseEntity<EventDto> create(@RequestBody EventDto event, UriComponentsBuilder uriBuilder) {
        EventDto result = eventService.create(event);

        return ResponseEntity.created(
                uriBuilder.path("/eventUid/{eventId}").build(result.getUid())
        ).body(result);
    }

    @GetMapping(path = "/event/cancellationReasons")
    public ResponseEntity<Map<String, String>> getCancellationReasons() {
        return new ResponseEntity<>(CancellationReason.getKeyValuePairs(), OK);
    }
}
