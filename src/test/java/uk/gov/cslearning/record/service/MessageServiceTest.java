package uk.gov.cslearning.record.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.cslearning.record.config.LpgUiConfig;
import uk.gov.cslearning.record.config.NotificationTemplates;
import uk.gov.cslearning.record.csrs.domain.CivilServant;
import uk.gov.cslearning.record.csrs.service.RegistryService;
import uk.gov.cslearning.record.domain.Booking;
import uk.gov.cslearning.record.domain.Learner;
import uk.gov.cslearning.record.dto.BookingCancellationReason;
import uk.gov.cslearning.record.dto.InviteDto;
import uk.gov.cslearning.record.notifications.dto.IMessageParams;
import uk.gov.cslearning.record.notifications.dto.NotificationTemplate;
import uk.gov.cslearning.record.service.catalogue.Module;
import uk.gov.cslearning.record.service.catalogue.*;
import uk.gov.cslearning.record.util.IUtilService;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class MessageServiceTest {
    private static final Course course = new Course("id", "title", List.of(), List.of());

    static {
        Venue venue = new Venue();
        venue.setLocation("London");

        DateRange dateRange = new DateRange();
        dateRange.setDate(LocalDate.of(2025, 2, 26));
        ArrayList<DateRange> dateRanges = new ArrayList<>();
        dateRanges.add(dateRange);

        Event event = new Event();
        event.setId("eventId");
        event.setDateRanges(dateRanges);
        event.setVenue(venue);

        Module module = new Module();
        module.setCost(BigDecimal.valueOf(10L));
        module.setId("moduleId");
        module.setEvents(List.of(event));
        course.setModules(List.of(module));
    }

    @Mock
    private LearningCatalogueService learningCatalogueService;
    @Mock
    private NotificationTemplates notificationTemplates;
    @Mock
    private RegistryService registryService;
    @Mock
    private LpgUiConfig lpgUiConfig;

    @Mock
    private IUtilService utilService;

    @InjectMocks
    private MessageService messageService;

    @BeforeEach
    public void before() {
        when(utilService.generateUUID()).thenReturn("UID");
        when(notificationTemplates.getTemplate(any())).thenReturn("Template");
        when(learningCatalogueService.getCourse("courseId")).thenReturn(course);
        when(lpgUiConfig.getBookingUrl(any(), any())).thenReturn("bookingUrl");
    }

    private void validateIMessageParams(IMessageParams params, String expectedRecipient,
                                        Map<String, String> expectedPersonalisation, NotificationTemplate expectedTemplate) {
        assertEquals(expectedRecipient, params.getRecipient());
        assertEquals(expectedTemplate, params.getTemplate());
        assertEquals(expectedPersonalisation, params.getPersonalisation());
    }

    @Test
    public void shouldCreateInviteMessage() throws URISyntaxException {
        InviteDto inviteDto = new InviteDto();
        inviteDto.setEvent(new URI("http://host/course/courseId/module/moduleId/event/eventId"));
        inviteDto.setLearnerEmail("test@domain.com");

        IMessageParams messageDto = messageService.createInviteMessage(inviteDto);
        validateIMessageParams(messageDto, "test@domain.com", Map.of(
                "learnerName", "test@domain.com",
                "courseTitle", "title",
                "courseDate", "26 Feb 2025",
                "courseLocation", "London",
                "inviteLink", "bookingUrl"
        ), NotificationTemplate.INVITE_LEARNER);
        verify(learningCatalogueService).getCourse("courseId");
    }

    @Test
    public void shouldCreateUnregisterMessage() throws URISyntaxException {
        Learner learner = new Learner("learnerId", "test@domain.com");
        Booking booking = new Booking();
        uk.gov.cslearning.record.domain.Event event = new uk.gov.cslearning.record.domain.Event();
        event.setPath("host/course/courseId/module/moduleId/event/eventId");
        booking.setEvent(event);
        booking.setLearner(learner);
        booking.setCancellationReason(BookingCancellationReason.PAYMENT);
        booking.setBookingReference("reference");

        CivilServant civilServant = new CivilServant();
        civilServant.setLineManagerEmailAddress("lmEmail@domain.com");
        civilServant.setFullName("Learner");
        when(registryService.getCivilServantResourceByUid(any())).thenReturn(Optional.of(civilServant));

        List<IMessageParams> messageDtos = messageService.createCancelBookingMessages(booking);
        IMessageParams learnerMessage = messageDtos.get(0);
        validateIMessageParams(learnerMessage, "test@domain.com", Map.of(
                "courseTitle", "title",
                "learnerName", "test@domain.com",
                "cancellationReason", "the booking has not been paid",
                "courseDate", "26 Feb 2025",
                "courseLocation", "London",
                "bookingReference", "reference"
        ), NotificationTemplate.CANCEL_BOOKING);
        IMessageParams lmMessage = messageDtos.get(1);
        validateIMessageParams(lmMessage, "lmEmail@domain.com", Map.of(
                "recipient", "lmEmail@domain.com",
                "learnerName", "Learner",
                "learnerEmail", "test@domain.com",
                "courseTitle", "title",
                "courseDate", "26 Feb 2025",
                "courseLocation", "London",
                "cost", "10",
                "bookingReference", "reference"
        ), NotificationTemplate.BOOKING_CANCELLED_LINE_MANAGER);
    }

    @Test
    public void shouldCreateBookingCreatedMessages() throws URISyntaxException {
        Learner learner = new Learner("learnerId", "test@domain.com");
        Booking booking = new Booking();
        uk.gov.cslearning.record.domain.Event event = new uk.gov.cslearning.record.domain.Event();
        event.setPath("host/course/courseId/module/moduleId/event/eventId");
        booking.setEvent(event);
        booking.setLearner(learner);
        booking.setBookingReference("reference");
        booking.setAccessibilityOptions("Accessibility option");

        CivilServant cs = new CivilServant();
        cs.setLineManagerEmailAddress("lmEmail@domain.com");
        cs.setFullName("Learner");
        when(registryService.getCivilServantResourceByUid("learnerId")).thenReturn(Optional.of(cs));

        List<IMessageParams> messageDtos = messageService.createRegisteredMessages(booking);

        IMessageParams learnerMessage = messageDtos.get(0);
        validateIMessageParams(learnerMessage, "test@domain.com", Map.of(
                "learnerName", "test@domain.com",
                "courseTitle", "title",
                "courseDate", "26 Feb 2025",
                "courseLocation", "London",
                "accessibility", "Accessibility option",
                "bookingReference", "reference"
        ), NotificationTemplate.BOOKING_REQUESTED);
        IMessageParams lmMessage = messageDtos.get(1);
        validateIMessageParams(lmMessage, "lmEmail@domain.com", Map.of(
                "recipient", "lmEmail@domain.com",
                "learnerName", "Learner",
                "learnerEmail", "test@domain.com",
                "courseTitle", "title",
                "courseDate", "26 Feb 2025",
                "courseLocation", "London",
                "cost", "10",
                "bookingReference", "reference"
        ), NotificationTemplate.BOOKING_REQUEST_LINE_MANAGER);
    }

    @Test
    public void shouldCreateBookingConfirmedMessages() throws URISyntaxException {
        Learner learner = new Learner("learnerId", "test@domain.com");
        Booking booking = new Booking();
        uk.gov.cslearning.record.domain.Event event = new uk.gov.cslearning.record.domain.Event();
        event.setPath("host/course/courseId/module/moduleId/event/eventId");
        booking.setEvent(event);
        booking.setLearner(learner);
        booking.setAccessibilityOptions("Braille");
        booking.setBookingReference("reference");

        CivilServant cs = new CivilServant();
        cs.setLineManagerEmailAddress("lmEmail@domain.com");
        cs.setFullName("Learner");
        when(registryService.getCivilServantResourceByUid("learnerId")).thenReturn(Optional.of(cs));

        List<IMessageParams> messageDtos = messageService.createBookedMessages(booking);

        IMessageParams learnerMessage = messageDtos.get(0);
        validateIMessageParams(learnerMessage, "test@domain.com", Map.of(
                "learnerName", "test@domain.com",
                "courseTitle", "title",
                "courseDate", "26 Feb 2025",
                "courseLocation", "London",
                "accessibility", "Braille",
                "bookingReference", "reference"
        ), NotificationTemplate.BOOKING_CONFIRMED);
        IMessageParams lmMessage = messageDtos.get(1);
        validateIMessageParams(lmMessage, "lmEmail@domain.com", Map.of(
                "recipient", "lmEmail@domain.com",
                "learnerName", "Learner",
                "learnerEmail", "test@domain.com",
                "courseTitle", "title",
                "courseDate", "26 Feb 2025",
                "courseLocation", "London",
                "cost", "10",
                "bookingReference", "reference"
        ), NotificationTemplate.BOOKING_CONFIRMED_LINE_MANAGER);
    }
}
