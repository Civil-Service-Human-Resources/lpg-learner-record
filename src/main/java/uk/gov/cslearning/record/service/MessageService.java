package uk.gov.cslearning.record.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.csrs.domain.CivilServant;
import uk.gov.cslearning.record.domain.Booking;
import uk.gov.cslearning.record.dto.BookingDto;
import uk.gov.cslearning.record.dto.InviteDto;
import uk.gov.cslearning.record.notifications.dto.MessageDto;
import uk.gov.cslearning.record.notifications.dto.factory.MessageDtoFactory;
import uk.gov.cslearning.record.service.catalogue.Course;
import uk.gov.cslearning.record.service.catalogue.Event;
import uk.gov.cslearning.record.service.catalogue.LearningCatalogueService;
import uk.gov.cslearning.record.service.catalogue.Module;

import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.Collection;
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
    private final String bookingRequestedMessageTemplateId;
    private final String bookingConfirmedMessageLineManagerTemplateId;
    private final String bookingCancelledMessageLineManagerTemplateId;
    private final String bookingRequestMessageLineManagerTemplateId;

    public MessageService(LearningCatalogueService learningCatalogueService,
                          MessageDtoFactory messageDtoFactory,
                          @Value("${lpg-ui.bookingUrlFormat}") String bookingUrlFormat,
                          @Value("${catalogue.serviceUrl}") String learningCatalogueBaseUrl,
                          @Value("${govNotify.template.inviteLearner}") String inviteMessageTemplateId,
                          @Value("${govNotify.template.cancelBooking}") String unregisterMessageTemplateId,
                          @Value("${govNotify.template.cancelEvent}") String cancelEventMessageTemplateId,
                          @Value("${govNotify.template.bookingConfirmed}") String bookingConfirmedMessageTemplateId,
                          @Value("${govNotify.template.bookingConfirmedLineManager}") String bookingConfirmedMessageLineManagerTemplateId,
                          @Value("${govNotify.template.bookingCancelledLineManager}") String bookingCancelledMessageLineManagerTemplateId,
                          @Value("${govNotify.template.bookingRequestLineManager}") String bookingRequestMessageLineManagerTemplateId,
                          @Value("${govNotify.template.bookingRequested}") String bookingRequestedMessageTemplateId
    ) {
        this.learningCatalogueService = learningCatalogueService;
        this.messageDtoFactory = messageDtoFactory;

        this.bookingUrlFormat = bookingUrlFormat;
        this.learningCatalogueBaseUrl = learningCatalogueBaseUrl;

        this.inviteMessageTemplateId = inviteMessageTemplateId;
        this.unregisterMessageTemplateId = unregisterMessageTemplateId;
        this.cancelEventMessageTemplateId = cancelEventMessageTemplateId;
        this.bookingConfirmedMessageTemplateId = bookingConfirmedMessageTemplateId;
        this.bookingConfirmedMessageLineManagerTemplateId = bookingConfirmedMessageLineManagerTemplateId;
        this.bookingCancelledMessageLineManagerTemplateId = bookingCancelledMessageLineManagerTemplateId;
        this.bookingRequestedMessageTemplateId = bookingRequestedMessageTemplateId;
        this.bookingRequestMessageLineManagerTemplateId = bookingRequestMessageLineManagerTemplateId;

    }

    public MessageDto createInviteMessage(InviteDto inviteDto) {
        Course course = getCourseByEventUrl(inviteDto.getEvent().getPath());
        Event event = learningCatalogueService.getEventByUrl(inviteDto.getEvent().toString());

        String[] parts = inviteDto.getEvent().getPath().split("/");
        String courseId = parts[parts.length - 5];
        String moduleId = parts[parts.length - 3];

        Map<String, String> map = createGenericMapForEvent(event, course, inviteDto.getLearnerEmail());
        map.put("inviteLink", String.format(bookingUrlFormat, courseId, moduleId));

        return messageDtoFactory.create(inviteDto.getLearnerEmail(), inviteMessageTemplateId, map);
    }

    public MessageDto createUnregisterMessage(BookingDto bookingDto, String cancellationReason) {
        Course course = getCourseByEventUrl(bookingDto.getEvent().getPath());
        Event event = learningCatalogueService.getEventByUrl(bookingDto.getEvent().toString());

        String eventUid = Paths.get(bookingDto.getEvent().toString()).getFileName().toString();
        String bookingReference = bookingDto.getBookingReference();

        Map<String, String> map = createGenericMapForEvent(event, course, bookingDto.getLearnerEmail());
        map.put("bookingReference", bookingReference);
        map.put("cancellationReason", cancellationReason);

        return messageDtoFactory.create(bookingDto.getLearnerEmail(), unregisterMessageTemplateId, map);
    }

    public MessageDto createCancelEventMessage(Booking booking, String cancellationReason) {
        String eventUrl = learningCatalogueBaseUrl + booking.getEvent().getPath();

        Course course = getCourseByEventUrl(eventUrl);
        Event event = learningCatalogueService.getEventByUrl(eventUrl);

        String bookingReference = booking.getBookingReference();

        Map<String, String> map = createGenericMapForEvent(event, course, booking.getLearner().getLearnerEmail());
        map.put("cancellationReason", cancellationReason);
        map.put("bookingReference", bookingReference);

        return messageDtoFactory.create(booking.getLearner().getLearnerEmail(), cancelEventMessageTemplateId, map);
    }

    public MessageDto createRegisteredMessage(BookingDto bookingDto) {
        Map<String, String> map = createGenericMapForBooking(bookingDto);
        return messageDtoFactory.create(bookingDto.getLearnerEmail(), bookingRequestedMessageTemplateId, map);
    }

    public MessageDto createBookedMessageForLineManager(BookingDto bookingDto, CivilServant civilServant) {
        return messageDtoFactory.create(civilServant.getLineManagerEmailAddress(), bookingConfirmedMessageLineManagerTemplateId, createMapForLineManagerTemplateEmail(bookingDto, civilServant));
    }

    public MessageDto createCancelledMessageForLineManager(BookingDto bookingDto, CivilServant civilServant) {
        return messageDtoFactory.create(civilServant.getLineManagerEmailAddress(), bookingCancelledMessageLineManagerTemplateId, createMapForLineManagerTemplateEmail(bookingDto, civilServant));
    }

    public MessageDto createRegisteredMessageForLineManager(BookingDto bookingDto, CivilServant civilServant) {
        return messageDtoFactory.create(civilServant.getLineManagerEmailAddress(), bookingRequestMessageLineManagerTemplateId, createMapForLineManagerTemplateEmail(bookingDto, civilServant));
    }

    private Map<String, String> createMapForLineManagerTemplateEmail(BookingDto bookingDto, CivilServant civilServant) {
        Map<String, String> map = new HashMap<String, String>();

        Course course = getCourseByEventUrl(bookingDto.getEvent().getPath());
        Event event = learningCatalogueService.getEventByUrl(bookingDto.getEvent().toString());

        String cost = getCostOfEvent(course, event.getId()).toString();

        map.put("email address", civilServant.getLineManagerEmailAddress());
        map.put("learnerName", civilServant.getFullName());
        map.put("learnerEmail", bookingDto.getLearnerEmail());

        map.put("recipient", civilServant.getLineManagerEmailAddress());
        map.put("courseTitle", course.getTitle());
        map.put("courseDate", event.getDateRanges().get(0).getDate().toString());

        map.put("courseLocation", event.getVenue().getLocation());
        map.put("cost", cost);

        map.put("bookingReference", bookingDto.getBookingReference());

        return map;
    }

    private BigDecimal getCostOfEvent(Course course, String eventID) {

        for (Module module : course.getModules()) {
            if (module.getModuleType().equals("face-to-face") &&
                    checkEvents(module.getEvents(), eventID)) {
                return module.getCost();
            }
        }
        return null;
    }

    private boolean checkEvents(Collection<Event> events, String eventID) {
        if (events != null && !events.isEmpty()) {
            return events.stream().anyMatch(event -> event.getId().equals(eventID));
        }
        return false;
    }

    public MessageDto createBookedMessage(BookingDto bookingDto) {
        Map<String, String> map = createGenericMapForBooking(bookingDto);

        return messageDtoFactory.create(bookingDto.getLearnerEmail(), bookingConfirmedMessageTemplateId, map);
    }

    private Map<String, String> createGenericMapForBooking(BookingDto bookingDto) {
        Course course = getCourseByEventUrl(bookingDto.getEvent().getPath());
        Event event = learningCatalogueService.getEventByUrl(bookingDto.getEvent().toString());

        String bookingReference = bookingDto.getBookingReference();

        Map<String, String> map = createGenericMapForEvent(event, course, bookingDto.getLearnerEmail());
        map.put("accessibility", bookingDto.getAccessibilityOptions());
        map.put("bookingReference", bookingReference);

        return map;
    }

    private Course getCourseByEventUrl(String eventUrl) {
        String[] parts = eventUrl.split("/");
        String courseId = parts[parts.length - 5];

        return learningCatalogueService.getCourse(courseId);
    }

    private Map<String, String> createGenericMapForEvent(Event event, Course course, String learnerEmail) {
        Map<String, String> map = new HashMap<>();
        map.put("learnerName", learnerEmail);
        map.put("courseTitle", course.getTitle());
        map.put("courseDate", event.getDateRanges().get(0).getDate().toString());
        map.put("courseLocation", event.getVenue().getLocation());

        return map;
    }


}
