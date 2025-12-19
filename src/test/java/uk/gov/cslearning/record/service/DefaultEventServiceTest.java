package uk.gov.cslearning.record.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.domain.factory.EventFactory;
import uk.gov.cslearning.record.dto.CancellationReason;
import uk.gov.cslearning.record.dto.EventDto;
import uk.gov.cslearning.record.dto.EventStatus;
import uk.gov.cslearning.record.dto.EventStatusDto;
import uk.gov.cslearning.record.dto.factory.EventDtoFactory;
import uk.gov.cslearning.record.exception.EventNotFoundException;
import uk.gov.cslearning.record.notifications.service.NotificationService;
import uk.gov.cslearning.record.repository.EventRepository;
import uk.gov.cslearning.record.util.UtilService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class DefaultEventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UtilService utilService;

    @Mock
    private EventFactory eventFactory;

    @Mock
    private EventDtoFactory eventDtoFactory;

    @Mock
    private BookingService bookingService;

    @Mock
    private NotificationService notificationService;
    @Mock
    private MessageService messageService;

    @InjectMocks
    private DefaultEventService eventService;

    @Test
    public void shouldGetEventAndCreateIfMissing() {
        String eventUid = "test-id";
        String path = "/test/test-id";

        Event event = new Event();

        Mockito.when(eventRepository.findByUid(eventUid)).thenReturn(Optional.of(event));

        assertEquals(event, eventService.getEventAndCreateIfMissing(eventUid));
    }

    @Test
    public void shouldCreateEventIfNotPresent() {
        String eventUid = "test-id";

        Event event = new Event();

        when(eventRepository.findByUid(eventUid)).thenReturn(Optional.empty()).thenReturn(Optional.of(event));
        when(eventFactory.create(eventUid)).thenReturn(event);
        when(eventRepository.save(event)).thenReturn(event);

        assertEquals(event, eventService.getEventAndCreateIfMissing(eventUid));
        verify(eventRepository).save(event);
    }

    @Test
    public void shouldThrowEventNotFoundException() {
        String eventUid = "eventUid-id";

        when(eventRepository.findByUid(eventUid)).thenReturn(Optional.empty());

        try {
            eventService.updateStatus(eventUid, new EventStatusDto(EventStatus.CANCELLED, CancellationReason.UNAVAILABLE));
            fail("Expected EventNotFoundException");
        } catch (EventNotFoundException e) {
            assertEquals("Event does not exist with catalogue id: " + eventUid, e.getMessage());
        }
    }

    @Test
    public void shouldCreateNewEvent() {
        EventDto eventDto = new EventDto();
        Event event = new Event();

        when(eventFactory.create(eventDto)).thenReturn(event);
        when(eventRepository.save(event)).thenReturn(event);
        when(eventDtoFactory.create(event)).thenReturn(eventDto);

        assertEquals(eventService.create(eventDto), eventDto);

        verify(eventFactory).create(eventDto);
        verify(eventRepository).save(event);
        verify(eventDtoFactory).create(event);
    }
}
