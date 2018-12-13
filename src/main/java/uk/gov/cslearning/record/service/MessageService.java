package uk.gov.cslearning.record.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.dto.BookingDto;
import uk.gov.cslearning.record.dto.InviteDto;
import uk.gov.cslearning.record.notifications.dto.MessageDto;
import uk.gov.cslearning.record.notifications.dto.factory.MessageDtoFactory;
import uk.gov.cslearning.record.service.catalogue.Course;
import uk.gov.cslearning.record.service.catalogue.Event;
import uk.gov.cslearning.record.service.catalogue.LearningCatalogueService;

import java.net.URI;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
public class MessageService {

    private final LearningCatalogueService learningCatalogueService;
    private final MessageDtoFactory messageDtoFactory;

    private final String bookingUrlFormat;

    private final String inviteMessageTemplateId;
    private final String unregisterMessageTemplateId;
    private final String cancelEventMessageTemplateId;

    public MessageService(LearningCatalogueService learningCatalogueService,
                          MessageDtoFactory messageDtoFactory,
                          @Value("${lpg-ui.bookingUrlFormat}") String bookingUrlFormat,
                          @Value("${govNotify.template.inviteLearner}") String inviteMessageTemplateId,
                          @Value("${govNotify.template.cancelBooking}") String unregisterMessageTemplateId,
                          @Value("${govNotify.template.cancelEvent}") String cancelEventMessageTemplateId
    ){
        this.learningCatalogueService = learningCatalogueService;
        this.messageDtoFactory = messageDtoFactory;

        this.bookingUrlFormat = bookingUrlFormat;

        this.inviteMessageTemplateId = inviteMessageTemplateId;
        this.unregisterMessageTemplateId = unregisterMessageTemplateId;
        this.cancelEventMessageTemplateId = cancelEventMessageTemplateId;
    }

    public MessageDto createInviteMessage(InviteDto inviteDto){
        Course course = getCourseByEventUrl(inviteDto.getEvent());
        Event event = learningCatalogueService.getEventByUrl(inviteDto.getEvent().toString());

        String[] parts = inviteDto.getEvent().getPath().split("/");
        String courseId = parts[parts.length - 5];
        String moduleId = parts[parts.length - 3];

        Map<String, String> map = createGenericMapForEvent(event, course, inviteDto.getLearnerEmail());
        map.put("inviteLink", String.format(bookingUrlFormat, courseId, moduleId));

        return messageDtoFactory.create(inviteDto.getLearnerEmail(), inviteMessageTemplateId, map);
    }

    public MessageDto createUnregisterMessage(BookingDto bookingDto){
        Course course = getCourseByEventUrl(bookingDto.getEvent());
        Event event = learningCatalogueService.getEventByUrl(bookingDto.getEvent().toString());
        String bookingReference = createBookingReference(bookingDto.getLearner(), bookingDto.getEvent());

        Map<String, String> map = createGenericMapForEvent(event, course, bookingDto.getLearnerEmail());
        map.put("bookingReference", bookingReference);

        return messageDtoFactory.create(bookingDto.getLearnerEmail(), unregisterMessageTemplateId, map);
    }

    public MessageDto createCancelEventMessage(BookingDto bookingDto, String cancellationReason){
        Course course = getCourseByEventUrl(bookingDto.getEvent());
        Event event = learningCatalogueService.getEventByUrl(bookingDto.getEvent().toString());
        String bookingReference = createBookingReference(bookingDto.getLearner(), bookingDto.getEvent());

        Map<String, String> map = createGenericMapForEvent(event, course, bookingDto.getLearnerEmail());
        map.put("cancellationReason", cancellationReason);
        map.put("bookingReference", bookingReference);

        return messageDtoFactory.create(bookingDto.getLearnerEmail(), cancelEventMessageTemplateId, map);
    }

    private Course getCourseByEventUrl(URI eventUrl){
        String[] parts = eventUrl.getPath().split("/");
        String courseId = parts[parts.length - 5];

        return learningCatalogueService.getCourse(courseId);
    }

    private Map<String, String> createGenericMapForEvent(Event event, Course course, String learnerEmail){
        Map<String, String> map = new HashMap<>();
        map.put("learnerName", learnerEmail);
        map.put("courseTitle", course.getTitle());
        map.put("courseDate", event.getDateRanges().get(0).getDate().toString());
        map.put("courseLocation", event.getVenue().getLocation());

        return map;
    }

    private String createBookingReference(String learnerId, URI eventUri){
        String eventId = Paths.get(eventUri.toString()).getFileName().toString();
        return learnerId + "-" + eventId;
    }
}
