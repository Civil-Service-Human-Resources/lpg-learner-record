package uk.gov.cslearning.record.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.domain.factory.InviteFactory;
import uk.gov.cslearning.record.dto.InviteDto;
import uk.gov.cslearning.record.dto.factory.InviteDtoFactory;
import uk.gov.cslearning.record.notifications.dto.MessageDto;
import uk.gov.cslearning.record.notifications.dto.factory.MessageDtoFactory;
import uk.gov.cslearning.record.notifications.service.NotificationService;
import uk.gov.cslearning.record.repository.InviteRepository;
import uk.gov.cslearning.record.service.catalogue.Course;
import uk.gov.cslearning.record.service.catalogue.LearningCatalogueService;

import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DefaultInviteService implements InviteService{
    private final InviteFactory inviteFactory;
    private final InviteDtoFactory inviteDtoFactory;
    private final InviteRepository inviteRepository;
    private final EventService eventService;
    private final NotificationService notificationService;
    private final MessageDtoFactory messageDtoFactory;
    private final LearningCatalogueService learningCatalogueService;

    private final String messageTemplateId;

    public DefaultInviteService(
            InviteFactory inviteFactory,
            InviteDtoFactory inviteDtoFactory,
            InviteRepository inviteRepository,
            EventService eventService,
            NotificationService notificationService,
            MessageDtoFactory messageDtoFactory,
            LearningCatalogueService learningCatalogueService,
            @Value("govNotify.template.inviteLearner") String messageTemplateId
    ){
        this.inviteFactory = inviteFactory;
        this.inviteDtoFactory = inviteDtoFactory;
        this.inviteRepository = inviteRepository;
        this.eventService = eventService;
        this.notificationService = notificationService;
        this.messageDtoFactory = messageDtoFactory;
        this.learningCatalogueService = learningCatalogueService;

        this.messageTemplateId = messageTemplateId;
    }

    @Override
    public Collection<InviteDto> findByEventId(String eventId){
        Collection<InviteDto> result = inviteRepository.findAllByEventUid(eventId)
                .stream().map(
                        inviteDtoFactory::create
                ).collect(Collectors.toList());

        return result;
    }

    @Override
    public Optional<InviteDto> findInvite(int id){
        return inviteRepository.findById(id).map(inviteDtoFactory::create);
    }

    @Override
    public Optional<InviteDto> findByEventIdAndLearnerEmail(String eventUid, String learnerEmail){
        return inviteRepository.findByEventUidAndLearnerEmail(eventUid, learnerEmail).map(inviteDtoFactory::create);
    }

    @Override
    public Optional<InviteDto> inviteLearner(InviteDto inviteDto){
        MessageDto message = createInviteMessage(inviteDto);

        notificationService.send(message);

        return save(inviteDto);
    }

    private Optional<InviteDto> save(InviteDto inviteDto){
        Event event = eventService.getEvent(Paths.get(inviteDto.getEvent().getPath()).getFileName().toString(), inviteDto.getEvent().getPath());

        return Optional.of(inviteDtoFactory.create(inviteRepository.save(inviteFactory.create(inviteDto, event))));
    }

    private MessageDto createInviteMessage(InviteDto inviteDto) {
        uk.gov.cslearning.record.service.catalogue.Event catalogueEvent = learningCatalogueService.getEvent(inviteDto.getEvent().toString());

        String[] parts = inviteDto.getEvent().getPath().split("/");
        String courseId = parts[parts.length - 5];
        Course course = learningCatalogueService.getCourse(courseId);

        Map<String, String> map = new HashMap<>();
        map.put("learnerName", inviteDto.getLearnerEmail());
        map.put("courseTitle", course.getTitle());
        map.put("courseDate", catalogueEvent.getDateRanges().get(0).getDate().toString());
        map.put("courseLocation", catalogueEvent.getVenue().getLocation());
        map.put("accessibility", "TEST ACCESSIBILITY");
        map.put("bookingReference", "TEST REFERENCE");

        return messageDtoFactory.create(inviteDto.getLearnerEmail(), messageTemplateId, map);
    }
}