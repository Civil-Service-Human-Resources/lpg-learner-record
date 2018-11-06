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
    public ResponseEntity<Collection<Invite>> listInvitees(@PathVariable("eventId") String catalogueId){
        Collection<Invite> result = inviteRepository.findByEventId(catalogueId);
        
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/{eventId}/invitee")
    public ResponseEntity<Event> addInvitee(@PathVariable("eventId") String catalogueId, @RequestBody Invite invite, UriComponentsBuilder builder){
        Optional<Event> result = eventRepository.findByCatalogueId(catalogueId);

        if(!result.isPresent()){
            createEvent(catalogueId, invite);
        }

        Event event = result.get();

        invite.setEvent(event);
        inviteRepository.save(invite);

        return ResponseEntity.created(builder.path("/event/{eventId}/invitee").build(catalogueId)).build();
    }

    private void createEvent(String catalogueId, Invite invite){
        Event event = new Event();
        event.setPath(invite.getEvent().getPath());
        event.setEventUid(catalogueId);
        eventRepository.save(event);
    }
}
