package uk.gov.cslearning.record.service;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.domain.Invite;
import uk.gov.cslearning.record.dto.InviteDto;
import uk.gov.cslearning.record.dto.factory.InviteDtoFactory;
import uk.gov.cslearning.record.exception.ResourceExists.ResourceExistsException;
import uk.gov.cslearning.record.repository.InviteRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DefaultInviteService implements InviteService {
    private final InviteDtoFactory inviteDtoFactory;
    private final InviteRepository inviteRepository;
    private final EventService eventService;

    public DefaultInviteService(InviteDtoFactory inviteDtoFactory, InviteRepository inviteRepository,
                                EventService eventService) {
        this.inviteDtoFactory = inviteDtoFactory;
        this.inviteRepository = inviteRepository;
        this.eventService = eventService;
    }

    @Override
    public Collection<InviteDto> findByEventId(String eventId) {
        return inviteRepository.findAllByEventUid(eventId)
                .stream().map(
                        inviteDtoFactory::create
                ).collect(Collectors.toList());
    }

    @Override
    public Optional<InviteDto> findInvite(int id) {
        return inviteRepository.findById(id).map(inviteDtoFactory::create);
    }

    @Override
    public InviteDto save(String eventUid, InviteDto inviteDto) {
        Event event = eventService.getEventAndCreateIfMissing(eventUid);
        if (event.isLearnerBookedOrInvited(inviteDto.getLearnerUid())) {
            throw new ResourceExistsException(String.format("Learner %s is already booked or invited to this event", inviteDto.getLearnerUid()));
        }
        Invite invite = new Invite(inviteDto.getId(), event, inviteDto.getLearnerEmail(), inviteDto.getLearnerUid());
        return inviteDtoFactory.create(inviteRepository.save(invite));
    }

    @Override
    public void deleteByLearnerUid(String learnerUid) {
        inviteRepository.deleteAllByLearnerUid(learnerUid);
    }
}
