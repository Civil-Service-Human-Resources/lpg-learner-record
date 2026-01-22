package uk.gov.cslearning.record.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.cslearning.record.domain.Booking;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.domain.Invite;
import uk.gov.cslearning.record.dto.InviteDto;
import uk.gov.cslearning.record.dto.factory.InviteDtoFactory;
import uk.gov.cslearning.record.exception.ResourceExists.ResourceExistsException;
import uk.gov.cslearning.record.repository.InviteRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


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
    public void shouldThrowExceptionIfLearnerIsAlreadyInvited() {
        Invite invite = new Invite();
        invite.setLearnerUid("userUid");
        Event event = new Event();
        event.setInvites(List.of(invite));

        when(eventService.getEventAndCreateIfMissing("SAI")).thenReturn(event);

        InviteDto inviteDto = new InviteDto();
        inviteDto.setLearnerUid("userUid");
        assertThrows(ResourceExistsException.class, () -> inviteService.save("SAI", inviteDto));
    }

    @Test
    public void shouldThrowExceptionIfLearnerIsAlreadyBooked() {
        Booking booking = new Booking();
        booking.setLearnerUid("userUid");
        Event event = new Event();
        event.setBookings(List.of(booking));

        when(eventService.getEventAndCreateIfMissing("SAI")).thenReturn(event);

        InviteDto inviteDto = new InviteDto();
        inviteDto.setLearnerUid("userUid");
        assertThrows(ResourceExistsException.class, () -> inviteService.save("SAI", inviteDto));
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
