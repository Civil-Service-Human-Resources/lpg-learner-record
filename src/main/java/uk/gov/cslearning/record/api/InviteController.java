package uk.gov.cslearning.record.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cslearning.record.dto.InviteDto;
import uk.gov.cslearning.record.service.InviteService;
import uk.gov.cslearning.record.service.identity.IdentityService;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/event")
public class InviteController {

    private final InviteService inviteService;

    private final IdentityService identityService;

    public InviteController(InviteService inviteService, IdentityService identityService){
        this.inviteService = inviteService;
        this.identityService = identityService;
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
    public ResponseEntity<Object> addInvitee(@PathVariable("eventId") String eventUid, @RequestBody InviteDto inviteDto, UriComponentsBuilder builder){
        if(identityService.getIdentityByEmailAddress(inviteDto.getLearnerEmail()) == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return inviteService.save(inviteDto)
                .map(i -> ResponseEntity.created(builder.path("/event/{eventId}/invitee").build(eventUid)).build())
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.CONFLICT));
    }
}
