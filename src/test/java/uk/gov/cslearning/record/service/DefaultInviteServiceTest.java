package uk.gov.cslearning.record.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.domain.Invite;
import uk.gov.cslearning.record.domain.factory.InviteFactory;
import uk.gov.cslearning.record.dto.InviteDto;
import uk.gov.cslearning.record.dto.factory.InviteDtoFactory;
import uk.gov.cslearning.record.repository.InviteRepository;
import uk.gov.cslearning.record.service.identity.Identity;
import uk.gov.cslearning.record.service.identity.IdentityService;

import java.net.URI;
import java.util.ArrayList;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class DefaultInviteServiceTest {
    @Mock
    private InviteRepository inviteRepository;

    @Mock
    private InviteDtoFactory inviteDtoFactory;

    @Mock
    private InviteFactory inviteFactory;

    @Mock
    private EventService eventService;

    @Mock
    private IdentityService identityService;

    @InjectMocks
    private DefaultInviteService inviteService;

    @Test
    public void shouldFindInvitesEventById(){
        String eventId = "test-id";
        URI eventURI = UriComponentsBuilder.fromPath("http://test/test-id").build().toUri();
        Invite invite = new Invite();
        InviteDto inviteDto = new InviteDto();
        inviteDto.setEvent(eventURI);

        ArrayList<Invite> invites = new ArrayList<>();
        invites.add(invite);

        Mockito.when(inviteRepository.findByEventId(eventId)).thenReturn(invites);
        Mockito.when(inviteDtoFactory.create(invite)).thenReturn(inviteDto);

        Assert.assertEquals(inviteDto, ((ArrayList) inviteService.findByEventId(eventId)).get(0));
    }

    @Test
    public void shouldFindInvite(){
        int id = 99;
        Invite invite = new Invite();
        InviteDto inviteDto = new InviteDto();

        Mockito.when(inviteRepository.findById(id)).thenReturn(Optional.of(new Invite()));
        Mockito.when(inviteDtoFactory.create(invite)).thenReturn(inviteDto);

        Assert.assertEquals(inviteService.findInvite(id), inviteDto);

        Mockito.verify(inviteRepository).findById(id);
        Mockito.verify(inviteDtoFactory).create(invite);
    }

    @Test
    public void shouldReturnNullIfInviteNotPresent(){
        int id = 99;

        Mockito.when(inviteRepository.findById(id)).thenReturn(Optional.empty());

        Assert.assertNull(inviteService.findInvite(id));

        Mockito.verify(inviteRepository).findById(id);
    }

    @Test
    public void shouldSaveInvite(){
        String eventId = "test-id";
        URI eventURI = UriComponentsBuilder.fromPath("http://test/test-id").build().toUri();
        InviteDto inviteDto = new InviteDto();
        inviteDto.setId(99);
        inviteDto.setEvent(eventURI);
        inviteDto.setLearnerEmail("test@test.com");

        Event event = new Event();
        event.setId(1);
        event.setUid(eventId);
        event.setPath(eventURI.getPath());

        Invite invite = new Invite();
        invite.setEvent(event);

        Mockito.when(eventService.getEvent("test-id", "/test/test-id")).thenReturn(event);
        Mockito.when(inviteFactory.create(inviteDto, event)).thenReturn(invite);
        Mockito.when(inviteRepository.save(invite)).thenReturn(invite);
        Mockito.when(inviteDtoFactory.create(invite)).thenReturn(inviteDto);
        Mockito.when(identityService.getIdentityByEmailAddress("test@test.com")).thenReturn(new Identity());

        Assert.assertEquals(inviteService.save(inviteDto), inviteDto);
        Mockito.verify(inviteRepository).save(invite);
        Mockito.verify(identityService).getIdentityByEmailAddress("test@test.com");
    }

    @Test
    public void shouldReturnNullIfIdentityNotFound(){
        InviteDto inviteDto = new InviteDto();
        inviteDto.setLearnerEmail("test@test.com");

        Mockito.when(identityService.getIdentityByEmailAddress("test@test.com")).thenReturn(null);

        Assert.assertNull(inviteService.save(inviteDto));
        Mockito.verify(identityService).getIdentityByEmailAddress("test@test.com");
    }
}
