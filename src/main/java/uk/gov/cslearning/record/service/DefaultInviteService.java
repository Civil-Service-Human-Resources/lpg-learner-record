package uk.gov.cslearning.record.service;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.domain.factory.InviteFactory;
import uk.gov.cslearning.record.dto.InviteDto;
import uk.gov.cslearning.record.dto.factory.InviteDtoFactory;
import uk.gov.cslearning.record.notifications.service.NotificationService;
import uk.gov.cslearning.record.repository.InviteRepository;

import java.nio.file.Paths;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DefaultInviteService implements InviteService {
    private final InviteFactory inviteFactory;
    private final InviteDtoFactory inviteDtoFactory;
    private final InviteRepository inviteRepository;
    private final EventService eventService;
    private final NotificationService notificationService;
    private final MessageService messageService;

    public DefaultInviteService(
            InviteFactory inviteFactory,
            InviteDtoFactory inviteDtoFactory,
            InviteRepository inviteRepository,
            EventService eventService,
            NotificationService notificationService,
            MessageService messageService
    ) {
        this.inviteFactory = inviteFactory;
        this.inviteDtoFactory = inviteDtoFactory;
        this.inviteRepository = inviteRepository;
        this.eventService = eventService;
        this.notificationService = notificationService;
        this.messageService = messageService;
    }

    @Override
    public Collection<InviteDto> findByEventId(String eventId) {
        Collection<InviteDto> result = inviteRepository.findAllByEventUid(eventId)
                .stream().map(
                        inviteDtoFactory::create
                ).collect(Collectors.toList());

        return result;
    }

    @Override
    public Optional<InviteDto> findInvite(int id) {
        return inviteRepository.findById(id).map(inviteDtoFactory::create);
    }

    @Override
    public Optional<InviteDto> findByEventIdAndLearnerEmail(String eventUid, String learnerEmail) {
        return inviteRepository.findByEventUidAndLearnerEmail(eventUid, learnerEmail).map(inviteDtoFactory::create);
    }

    @Override
    public Optional<InviteDto> inviteLearner(InviteDto inviteDto) {
        notificationService.send(messageService.createInviteMessage(inviteDto));

        return save(inviteDto);
    }

    @Override
    public Optional<InviteDto> save(InviteDto inviteDto) {
        Event event = eventService.getEvent(Paths.get(inviteDto.getEvent().getPath()).getFileName().toString(), inviteDto.getEvent().getPath());

        return Optional.of(inviteDtoFactory.create(inviteRepository.save(inviteFactory.create(inviteDto, event))));
    }

    @Override
    public void deleteByLearnerEmail(String learnerEmail) {
        inviteRepository.deleteAllByLearnerEmail(learnerEmail);
    }
}