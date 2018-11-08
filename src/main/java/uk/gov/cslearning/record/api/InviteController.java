package uk.gov.cslearning.record.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.domain.Invite;
import uk.gov.cslearning.record.dto.InviteDto;
import uk.gov.cslearning.record.repository.EventRepository;
import uk.gov.cslearning.record.repository.InviteRepository;
import uk.gov.cslearning.record.service.InviteService;

import javax.ws.rs.core.UriBuilder;
import java.util.Collection;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

@RestController
@RequestMapping("/event")
public class InviteController {

    @Autowired
    EventRepository eventRepository;

    InviteService inviteService;

    public InviteController(InviteService inviteService, EventRepository eventRepository){
        this.inviteService = inviteService;
        this.eventRepository = eventRepository;
    }

    @GetMapping("/{eventId}/invitee")
    public ResponseEntity<Collection<InviteDto>> listInvitees(@PathVariable("eventId") String eventUid){
        Collection<InviteDto> result = inviteService.findByEventId(eventUid);
        
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/{eventId}/invitee")
    public ResponseEntity<Event> addInvitee(@PathVariable("eventId") String eventUid, @RequestBody InviteDto inviteDto, UriComponentsBuilder builder){
        if(!eventRepository.findByEventUid(eventUid).isPresent()){
            createEvent(eventUid, inviteDto);
        }

        inviteDto.setEvent(UriBuilder.fromUri(inviteDto.getEvent().getPath()).build());
        inviteService.save(inviteDto);

        return ResponseEntity.created(builder.path("/event/{eventId}/invitee").build(eventUid)).build();
    }

    private void createEvent(String eventUid, InviteDto inviteDto){
        Event event = new Event();
        event.setPath(inviteDto.getEvent().getPath());
        event.setEventUid(eventUid);
        eventRepository.save(event);
    }
}
