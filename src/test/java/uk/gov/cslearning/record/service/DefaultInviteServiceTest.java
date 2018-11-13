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

import java.net.URI;
import java.util.ArrayList;

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
    public void shouldSaveInvite(){
        String eventId = "test-id";
        URI eventURI = UriComponentsBuilder.fromPath("http://test/test-id").build().toUri();
        InviteDto inviteDto = new InviteDto();
        inviteDto.setId(99);
        inviteDto.setEvent(eventURI);
        inviteDto.setLearnerEmail("test@test.com");

        Event event = new Event();
        event.setId(1);
        event.setEventUid(eventId);
        event.setPath(eventURI.getPath());

        Invite invite = new Invite();
        invite.setEvent(event);

        Mockito.when(eventService.getEvent("test-id", "/test/test-id")).thenReturn(event);
        Mockito.when(inviteFactory.create(inviteDto, event)).thenReturn(invite);
        Mockito.when(inviteRepository.save(invite)).thenReturn(invite);
        Mockito.when(inviteDtoFactory.create(invite)).thenReturn(inviteDto);

        Assert.assertEquals(inviteService.save(inviteDto), inviteDto);
        Mockito.verify(inviteRepository).save(invite);
    }
}
