package uk.gov.cslearning.record.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.record.domain.Booking;
import uk.gov.cslearning.record.domain.Learner;
import uk.gov.cslearning.record.dto.BookingDto;
import uk.gov.cslearning.record.dto.InviteDto;
import uk.gov.cslearning.record.notifications.dto.MessageDto;
import uk.gov.cslearning.record.notifications.dto.factory.MessageDtoFactory;
import uk.gov.cslearning.record.service.catalogue.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class MessageServiceTest {
    private final LearningCatalogueService learningCatalogueService = mock(LearningCatalogueService.class);

    private final MessageDtoFactory messageDtoFactory = mock(MessageDtoFactory.class);

    private final String bookingUrlFormat = "test/format/%s/%s";
    private final String learningCatalogueBaseUrl = "http://host";

    private final String inviteMessageTemplateId = "inviteTemplateId";
    private final String unregisterMessageTemplateId = "unregisterTemplateId";
    private final String cancelEventMessageTemplateId = "cancelEventTemplateId";
    private final String bookingConfirmedMessageTemplateID = "bookingConfirmedTemplateId";

    private final MessageService messageService = new MessageService(learningCatalogueService, messageDtoFactory, bookingUrlFormat, learningCatalogueBaseUrl, inviteMessageTemplateId, unregisterMessageTemplateId, cancelEventMessageTemplateId, bookingConfirmedMessageTemplateID);

    @Test
    public void shouldCreateInviteMessage() throws URISyntaxException {
        InviteDto inviteDto = new InviteDto();
        inviteDto.setEvent(new URI("host/course/courseId/module/moduleId/event/eventId"));
        inviteDto.setLearnerEmail("test@domain.com");

        Course course = new Course();
        course.setTitle("title");

        Venue venue = new Venue();
        venue.setLocation("London");

        DateRange dateRange = new DateRange();
        dateRange.setDate(LocalDate.now());
        ArrayList<DateRange> dateRanges = new ArrayList<>();
        dateRanges.add(dateRange);

        Event event = new Event();
        event.setDateRanges(dateRanges);
        event.setVenue(venue);

        MessageDto messageDto = new MessageDto();

        when(learningCatalogueService.getCourse("courseId")).thenReturn(course);
        when(learningCatalogueService.getEventByUrl("host/course/courseId/module/moduleId/event/eventId")).thenReturn(event);
        when(messageDtoFactory.create(any(), any(), any())).thenReturn(messageDto);

        assertEquals(messageService.createInviteMessage(inviteDto), messageDto);

        verify(learningCatalogueService).getCourse("courseId");
        verify(learningCatalogueService).getEventByUrl("host/course/courseId/module/moduleId/event/eventId");
        verify(messageDtoFactory).create(any(), any(), any());
    }

    @Test
    public void shouldCreateUnregisterMessage() throws URISyntaxException {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setEvent(new URI("host/course/courseId/module/moduleId/event/eventId"));
        bookingDto.setLearnerEmail("test@domain.com");
        bookingDto.setLearner("learnerId");

        Course course = new Course();
        course.setTitle("title");

        Venue venue = new Venue();
        venue.setLocation("London");

        DateRange dateRange = new DateRange();
        dateRange.setDate(LocalDate.now());
        ArrayList<DateRange> dateRanges = new ArrayList<>();
        dateRanges.add(dateRange);

        Event event = new Event();
        event.setDateRanges(dateRanges);
        event.setVenue(venue);

        MessageDto messageDto = new MessageDto();

        when(learningCatalogueService.getCourse("courseId")).thenReturn(course);
        when(learningCatalogueService.getEventByUrl("host/course/courseId/module/moduleId/event/eventId")).thenReturn(event);
        when(messageDtoFactory.create(any(), any(), any())).thenReturn(messageDto);

        assertEquals(messageService.createUnregisterMessage(bookingDto), messageDto);

        verify(learningCatalogueService).getCourse("courseId");
        verify(learningCatalogueService).getEventByUrl("host/course/courseId/module/moduleId/event/eventId");
        verify(messageDtoFactory).create(any(), any(), any());
    }

    @Test
    public void shouldCreateCancelEventMessage() throws URISyntaxException {
        uk.gov.cslearning.record.domain.Event recordEvent = new uk.gov.cslearning.record.domain.Event();
        recordEvent.setPath("/course/courseId/module/moduleId/event/eventId");

        Learner learner = new Learner();
        learner.setLearnerEmail("test@domain.com");
        learner.setUid("learnerId");

        Booking booking = new Booking();
        booking.setLearner(learner);
        booking.setEvent(recordEvent);

        String cancellationReason = "cancellation reason";

        Course course = new Course();
        course.setTitle("title");

        Venue venue = new Venue();
        venue.setLocation("London");

        DateRange dateRange = new DateRange();
        dateRange.setDate(LocalDate.now());
        ArrayList<DateRange> dateRanges = new ArrayList<>();
        dateRanges.add(dateRange);

        Event event = new Event();
        event.setDateRanges(dateRanges);
        event.setVenue(venue);

        MessageDto messageDto = new MessageDto();

        when(learningCatalogueService.getCourse("courseId")).thenReturn(course);
        when(learningCatalogueService.getEventByUrl("http://host/course/courseId/module/moduleId/event/eventId")).thenReturn(event);
        when(messageDtoFactory.create(any(), any(), any())).thenReturn(messageDto);

        assertEquals(messageService.createCancelEventMessage(booking, cancellationReason), messageDto);

        verify(learningCatalogueService).getCourse("courseId");
        verify(learningCatalogueService).getEventByUrl("http://host/course/courseId/module/moduleId/event/eventId");
        verify(messageDtoFactory).create(any(), any(), any());
    }

    @Test
    public void shouldCreateBookingConfirmedMessage() throws URISyntaxException {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setEvent(new URI("host/course/courseId/module/moduleId/event/eventId"));
        bookingDto.setLearnerEmail("test@domain.com");
        bookingDto.setLearner("learnerId");

        Course course = new Course();
        course.setTitle("title");

        Venue venue = new Venue();
        venue.setLocation("London");

        DateRange dateRange = new DateRange();
        dateRange.setDate(LocalDate.now());
        ArrayList<DateRange> dateRanges = new ArrayList<>();
        dateRanges.add(dateRange);

        Event event = new Event();
        event.setDateRanges(dateRanges);
        event.setVenue(venue);

        MessageDto messageDto = new MessageDto();

        when(learningCatalogueService.getCourse("courseId")).thenReturn(course);
        when(learningCatalogueService.getEventByUrl("host/course/courseId/module/moduleId/event/eventId")).thenReturn(event);
        when(messageDtoFactory.create(any(), any(), any())).thenReturn(messageDto);

        assertEquals(messageService.createBookedMessage(bookingDto), messageDto);

        verify(learningCatalogueService).getCourse("courseId");
        verify(learningCatalogueService).getEventByUrl("host/course/courseId/module/moduleId/event/eventId");
        verify(messageDtoFactory).create(any(), any(), any());
    }
}
