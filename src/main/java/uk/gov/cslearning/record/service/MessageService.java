package uk.gov.cslearning.record.service;

import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.domain.Booking;
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
    private final String learningCatalogueBaseUrl;

    private final String inviteMessageTemplateId;
    private final String unregisterMessageTemplateId;
    private final String cancelEventMessageTemplateId;
    private final String bookingConfirmedMessageTemplateId;

    public MessageService(LearningCatalogueService learningCatalogueService,
                          MessageDtoFactory messageDtoFactory,
                          @Value("${lpg-ui.bookingUrlFormat}") String bookingUrlFormat,
                          @Value("${catalogue.serviceUrl}") String learningCatalogueBaseUrl,
                          @Value("${govNotify.template.inviteLearner}") String inviteMessageTemplateId,
                          @Value("${govNotify.template.cancelBooking}") String unregisterMessageTemplateId,
                          @Value("${govNotify.template.cancelEvent}") String cancelEventMessageTemplateId,
                          @Value("${govNotify.template.bookingConfirmed}") String bookingConfirmedMessageTemplateId
    ){
        this.learningCatalogueService = learningCatalogueService;
        this.messageDtoFactory = messageDtoFactory;

        this.bookingUrlFormat = bookingUrlFormat;
        this.learningCatalogueBaseUrl = learningCatalogueBaseUrl;

        this.inviteMessageTemplateId = inviteMessageTemplateId;
        this.unregisterMessageTemplateId = unregisterMessageTemplateId;
        this.cancelEventMessageTemplateId = cancelEventMessageTemplateId;
        this.bookingConfirmedMessageTemplateId = bookingConfirmedMessageTemplateId;
    }

    public MessageDto createInviteMessage(InviteDto inviteDto){
        Course course = getCourseByEventUrl(inviteDto.getEvent().getPath());
        Event event = learningCatalogueService.getEventByUrl(inviteDto.getEvent().toString());

        String[] parts = inviteDto.getEvent().getPath().split("/");
        String courseId = parts[parts.length - 5];
        String moduleId = parts[parts.length - 3];

        Map<String, String> map = createGenericMapForEvent(event, course, inviteDto.getLearnerEmail());
        map.put("inviteLink", String.format(bookingUrlFormat, courseId, moduleId));

        return messageDtoFactory.create(inviteDto.getLearnerEmail(), inviteMessageTemplateId, map);
    }

    public MessageDto createUnregisterMessage(BookingDto bookingDto, String cancellationReason){
        Course course = getCourseByEventUrl(bookingDto.getEvent().getPath());
        Event event = learningCatalogueService.getEventByUrl(bookingDto.getEvent().toString());

        String eventUid = Paths.get(bookingDto.getEvent().toString()).getFileName().toString();
        String bookingReference = createBookingReference(bookingDto.getLearner(), eventUid);

        Map<String, String> map = createGenericMapForEvent(event, course, bookingDto.getLearnerEmail());
        map.put("bookingReference", bookingReference);
        map.put("cancellationReason", cancellationReason);

        return messageDtoFactory.create(bookingDto.getLearnerEmail(), unregisterMessageTemplateId, map);
    }

    public MessageDto createCancelEventMessage(Booking booking, String cancellationReason){
        String eventUrl = learningCatalogueBaseUrl + booking.getEvent().getPath();

        Course course = getCourseByEventUrl(eventUrl);
        Event event = learningCatalogueService.getEventByUrl(eventUrl);

        String bookingReference = createBookingReference(booking.getLearner().getUid(), booking.getEvent().getUid());

        Map<String, String> map = createGenericMapForEvent(event, course, booking.getLearner().getLearnerEmail());
        map.put("cancellationReason", cancellationReason);
        map.put("bookingReference", bookingReference);

        return messageDtoFactory.create(booking.getLearner().getLearnerEmail(), cancelEventMessageTemplateId, map);
    }

    public MessageDto createBookedMessage(BookingDto bookingDto) {
        Course course = getCourseByEventUrl(bookingDto.getEvent().getPath());
        Event event = learningCatalogueService.getEventByUrl(bookingDto.getEvent().toString());

        String eventUid = Paths.get(bookingDto.getEvent().toString()).getFileName().toString();
        String bookingReference = createBookingReference(bookingDto.getLearner(), eventUid);

        Map<String, String> map = createGenericMapForEvent(event, course, bookingDto.getLearnerEmail());
        map.put("accessibility", bookingDto.getAccessibilityOptions());
        map.put("bookingReference", bookingReference);

        return messageDtoFactory.create(bookingDto.getLearnerEmail(), bookingConfirmedMessageTemplateId, map);
    }

    private Course getCourseByEventUrl(String eventUrl){
        String[] parts = eventUrl.split("/");
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

    private String createBookingReference(String learnerId, String eventId){
        return learnerId + "-" + eventId;
    }
}
