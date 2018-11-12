package uk.gov.cslearning.record.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cslearning.record.dto.InviteDto;
import uk.gov.cslearning.record.service.InviteService;

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

    @PostMapping("/{eventId}/invitee")
    public ResponseEntity<Void> addInvitee(@PathVariable("eventId") String eventUid, @RequestBody InviteDto inviteDto, UriComponentsBuilder builder){
        inviteService.save(inviteDto);

        return ResponseEntity.created(builder.path("/event/{eventId}/invitee").build(eventUid)).build();
    }
}
