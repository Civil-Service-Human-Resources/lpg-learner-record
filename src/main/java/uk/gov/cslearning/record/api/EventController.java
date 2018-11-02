package uk.gov.cslearning.record.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.domain.Invite;
import uk.gov.cslearning.record.repository.EventRepository;
import uk.gov.cslearning.record.repository.InviteRepository;

import static com.google.common.base.Preconditions.checkArgument;

@RestController
@RequestMapping("/event")
public class EventController {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    InviteRepository inviteRepository;

    public EventController(InviteRepository inviteRepository, EventRepository eventRepository){
        checkArgument(inviteRepository != null);
        checkArgument(eventRepository != null);
        this.inviteRepository = inviteRepository;
        this.eventRepository = eventRepository;
    }

    @PostMapping("/{eventId}/invitee")
    public ResponseEntity<Event> addInvitee(@PathVariable("eventId") String catalogueId, @RequestBody Invite invite, UriComponentsBuilder builder){
        if(!eventRepository.findByCatalogueId(catalogueId).isPresent()){
            return ResponseEntity.badRequest().build();
        }

        Event event = eventRepository.findByCatalogueId(catalogueId).get();

        invite.setEvent(event);
        inviteRepository.save(invite);

        return ResponseEntity.created(builder.path("/event/{eventId}/invitee/{inviteId}").build(catalogueId, invite.getId())).build();
    }
}
