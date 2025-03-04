package uk.gov.cslearning.record.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cslearning.record.domain.Invite;
import uk.gov.cslearning.record.dto.InviteDto;
import uk.gov.cslearning.record.dto.factory.InviteDtoFactory;
import uk.gov.cslearning.record.repository.InviteRepository;

import java.net.URI;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(SpringExtension.class)
public class DefaultInviteServiceTest {
    @Mock
    private InviteRepository inviteRepository;

    @Mock
    private InviteDtoFactory inviteDtoFactory;

    @Mock
    private EventService eventService;

    @InjectMocks
    private DefaultInviteService inviteService;

    @Test
    public void shouldFindInvitesEventById() {
        String eventId = "test-id";
        URI eventURI = UriComponentsBuilder.fromPath("http://test/test-id").build().toUri();
        Invite invite = new Invite();
        InviteDto inviteDto = new InviteDto();
        inviteDto.setEvent(eventURI);

        ArrayList<Invite> invites = new ArrayList<>();
        invites.add(invite);

        Mockito.when(inviteRepository.findAllByEventUid(eventId)).thenReturn(invites);
        Mockito.when(inviteDtoFactory.create(invite)).thenReturn(inviteDto);

        assertEquals(inviteDto, inviteService.findByEventId(eventId).stream().findFirst().get());
    }

    @Test
    public void shouldFindInvite() {
        int id = 99;
        Invite invite = new Invite();
        InviteDto inviteDto = new InviteDto();

        Mockito.when(inviteRepository.findById(id)).thenReturn(Optional.of(new Invite()));
        Mockito.when(inviteDtoFactory.create(invite)).thenReturn(inviteDto);

        assertEquals(Optional.of(inviteDto), inviteService.findInvite(id));

        Mockito.verify(inviteRepository).findById(id);
        Mockito.verify(inviteDtoFactory).create(invite);
    }

    @Test
    public void shouldReturnNullIfInviteNotPresent() {
        int id = 99;

        Mockito.when(inviteRepository.findById(id)).thenReturn(Optional.empty());

        assertEquals(Optional.empty(), inviteService.findInvite(id));

        Mockito.verify(inviteRepository).findById(id);
    }
}
