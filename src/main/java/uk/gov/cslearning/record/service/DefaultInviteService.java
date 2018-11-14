package uk.gov.cslearning.record.service;


import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.domain.Invite;
import uk.gov.cslearning.record.domain.factory.InviteFactory;
import uk.gov.cslearning.record.dto.InviteDto;
import uk.gov.cslearning.record.dto.factory.InviteDtoFactory;
import uk.gov.cslearning.record.repository.InviteRepository;
import uk.gov.cslearning.record.service.identity.Identity;
import uk.gov.cslearning.record.service.identity.IdentityService;

import java.nio.file.Paths;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DefaultInviteService implements InviteService{
    private final InviteFactory inviteFactory;
    private final InviteDtoFactory inviteDtoFactory;
    private final InviteRepository inviteRepository;
    private final EventService eventService;
    private final IdentityService identityService;

    public DefaultInviteService(InviteFactory inviteFactory, InviteDtoFactory inviteDtoFactory, InviteRepository inviteRepository, EventService eventService, IdentityService identityService){
        this.inviteFactory = inviteFactory;
        this.inviteDtoFactory = inviteDtoFactory;
        this.inviteRepository = inviteRepository;
        this.eventService = eventService;
        this.identityService = identityService;
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
    public InviteDto findInvite(int id){
        Optional<Invite> invite = inviteRepository.findById(id);

        return invite.isPresent() ? inviteDtoFactory.create(invite.get()) : null;
    }

    @Override
    public InviteDto save(InviteDto inviteDto){
        Identity identity = identityService.getIdentityByEmailAddress(inviteDto.getLearnerEmail());
        if(identity != null) {
            Event event = eventService.getEvent(Paths.get(inviteDto.getEvent().getPath()).getFileName().toString(), inviteDto.getEvent().getPath());

            Optional<Invite> invite = (event == null) ? Optional.empty() : inviteRepository.findByEventIdLearnerEmail(event.getId(), inviteDto.getLearnerEmail());

            if (!invite.isPresent()) {
                return inviteDtoFactory.create(inviteRepository.save(inviteFactory.create(inviteDto, event)));
            }
            return inviteDtoFactory.create(invite.get());
        }

        return null;
    }
}