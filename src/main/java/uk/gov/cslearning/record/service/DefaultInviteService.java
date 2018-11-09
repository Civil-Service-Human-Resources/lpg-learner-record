package uk.gov.cslearning.record.service;


import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.domain.factory.InviteFactory;
import uk.gov.cslearning.record.dto.InviteDto;
import uk.gov.cslearning.record.dto.factory.InviteDtoFactory;
import uk.gov.cslearning.record.repository.EventRepository;
import uk.gov.cslearning.record.repository.InviteRepository;

import java.nio.file.Paths;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class DefaultInviteService implements InviteService{
    private final InviteFactory inviteFactory;
    private final InviteDtoFactory inviteDtoFactory;
    private final InviteRepository inviteRepository;
    private final EventRepository eventRepository;
    private final EventService eventService;

    public DefaultInviteService(InviteFactory inviteFactory, InviteDtoFactory inviteDtoFactory, InviteRepository inviteRepository, EventRepository eventRepository, EventService eventService){
        this.inviteFactory = inviteFactory;
        this.inviteDtoFactory = inviteDtoFactory;
        this.inviteRepository = inviteRepository;
        this.eventRepository = eventRepository;
        this.eventService = eventService;
    }

    @Override
    public Collection<InviteDto> findByEventId(String eventId){
        Collection<InviteDto> result = inviteRepository.findByEventId(eventId)
                .stream().map(
                        inviteDtoFactory::create
                ).collect(Collectors.toList());


        return result;
    }

    @Override
    public InviteDto save(InviteDto inviteDto){
        Event event = eventService.getEvent(Paths.get(inviteDto.getEvent().getPath()).getFileName().toString(), inviteDto.getEvent().getPath());
        return inviteDtoFactory.create(inviteRepository.save(inviteFactory.create(inviteDto, event)));
    }
}