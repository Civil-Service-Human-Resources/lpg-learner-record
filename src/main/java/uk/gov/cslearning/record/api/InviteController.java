package uk.gov.cslearning.record.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.domain.Invite;
import uk.gov.cslearning.record.repository.EventRepository;
import uk.gov.cslearning.record.repository.InviteRepository;

import java.util.Collection;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

@RestController
@RequestMapping("/event")
public class InviteController {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    InviteRepository inviteRepository;

    public InviteController(InviteRepository inviteRepository, EventRepository eventRepository){
        checkArgument(inviteRepository != null);
        checkArgument(eventRepository != null);
        this.inviteRepository = inviteRepository;
        this.eventRepository = eventRepository;
    }

    @GetMapping("/{eventId}/invitee")
    public ResponseEntity<Collection<Invite>> listInvitees(@PathVariable("eventId") String eventUid){
        Collection<Invite> result = inviteRepository.findByEventId(eventUid);
        
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/{eventId}/invitee")
    public ResponseEntity<Event> addInvitee(@PathVariable("eventId") String eventUid, @RequestBody Invite invite, UriComponentsBuilder builder){
        if(!eventRepository.findByEventUid(eventUid).isPresent()){
            createEvent(eventUid, invite);
        }

        Event event = eventRepository.findByEventUid(eventUid).get();

        invite.setEvent(event);
        inviteRepository.save(invite);

        return ResponseEntity.created(builder.path("/event/{eventId}/invitee").build(eventUid)).build();
    }

    private void createEvent(String eventUid, Invite invite){
        Event event = new Event();
        event.setPath(invite.getEvent().getPath());
        event.setEventUid(eventUid);
        eventRepository.save(event);
    }
}
