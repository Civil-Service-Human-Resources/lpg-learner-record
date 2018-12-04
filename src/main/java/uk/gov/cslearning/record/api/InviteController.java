package uk.gov.cslearning.record.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cslearning.record.dto.InviteDto;
import uk.gov.cslearning.record.service.InviteService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/event")
public class InviteController {

    private final InviteService inviteService;

    public InviteController(InviteService inviteService){
        this.inviteService = inviteService;
    }

    @GetMapping("/{eventId}/invitee")
    public ResponseEntity<Collection<InviteDto>> listInvitees(@PathVariable("eventId") String eventUid){
        Collection<InviteDto> result = inviteService.findByEventId(eventUid);
        
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/{eventId}/invitee/{inviteId}")
    public ResponseEntity<InviteDto> getInvitee(@PathVariable("eventId") String eventUid, @PathVariable("inviteId") int inviteId){
        return inviteService.findInvite(inviteId)
                .map(i -> new ResponseEntity<>(i, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/{eventId}/invitee")
    public ResponseEntity<Object> addInvitee(@PathVariable("eventId") String eventUid, @Valid @RequestBody InviteDto inviteDto, UriComponentsBuilder builder){
        return inviteService.save(inviteDto)
                .map(i -> ResponseEntity.created(builder.path("/event/{eventId}/invitee").build(eventUid)).build())
                .orElseThrow(() -> new IllegalStateException("Unable to save invite"));
    }
}
