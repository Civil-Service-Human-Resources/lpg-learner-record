package uk.gov.cslearning.record.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.record.domain.Booking;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.domain.factory.EventFactory;
import uk.gov.cslearning.record.dto.EventDto;
import uk.gov.cslearning.record.dto.EventStatus;
import uk.gov.cslearning.record.dto.EventStatusDto;
import uk.gov.cslearning.record.dto.factory.EventDtoFactory;
import uk.gov.cslearning.record.exception.EventNotFoundException;
import uk.gov.cslearning.record.repository.EventRepository;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultEventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventFactory eventFactory;

    @Mock
    private EventDtoFactory eventDtoFactory;

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private DefaultEventService eventService;

    @Test
    public void shouldGetEvent(){
        String eventUid = "test-id";
        String path = "/test/test-id";

        Event event = new Event();

        Mockito.when(eventRepository.findByUid(eventUid)).thenReturn(Optional.of(event));

        Assert.assertEquals(eventService.getEvent(eventUid, path), event);
    }

    @Test
    public void shouldCreateEventIfNotPresent(){
        String eventUid = "test-id";
        String path = "/test/test-id";

        Event event = new Event();

        when(eventRepository.findByUid(eventUid)).thenReturn(Optional.empty()).thenReturn(Optional.of(event));
        when(eventFactory.create(path)).thenReturn(event);
        when(eventRepository.save(event)).thenReturn(event);

        Assert.assertEquals(eventService.getEvent(eventUid, path), event);
        verify(eventRepository).save(event);
    }

    @Test
    public void shouldThrowEventNotFoundException() {
        String eventUid = "event-id";

        when(eventRepository.findByUid(eventUid)).thenReturn(Optional.empty());

        try {
            eventService.updateStatus(eventUid, new EventStatusDto(EventStatus.CANCELLED));
            fail("Expected EventNotFoundException");
        } catch (EventNotFoundException e) {
            assertEquals("Event does not exist with catalogue id: " + eventUid, e.getMessage());
        }
    }

    @Test
    public void shouldFindEventByUid() {
        String eventUid = "event-id";

        Event event = new Event();
        EventDto eventDto = new EventDto();

        when(eventRepository.findByUid(eventUid)).thenReturn(Optional.of(event));
        when(eventDtoFactory.create(event)).thenReturn(eventDto);

        assertEquals(Optional.of(eventDto), eventService.findByUid(eventUid));
    }

    @Test
    public void shouldUpdateStatus() {
        String eventUid = "event-id";

        Event event = mock(Event.class);
        Event updatedEvent = mock(Event.class);
        Event savedEvent = mock(Event.class);
        EventDto eventDto = mock(EventDto.class);
        EventDto savedEventDto = mock(EventDto.class);

        Booking booking1 = new Booking();
        booking1.setId(1);
        Booking booking2 = new Booking();
        booking2.setId(2);

        when(event.getBookings()).thenReturn(Arrays.asList(booking1, booking2));

        when(eventRepository.findByUid(eventUid)).thenReturn(Optional.of(event));
        when(eventDtoFactory.create(event)).thenReturn(eventDto);
        when(eventFactory.create(eventDto)).thenReturn(updatedEvent);
        when(eventRepository.save(updatedEvent)).thenReturn(savedEvent);
        when(eventDtoFactory.create(savedEvent)).thenReturn(savedEventDto);

        assertEquals(Optional.of(savedEventDto), eventService.updateStatus(eventUid, new EventStatusDto(EventStatus.CANCELLED)));

        verify(eventDto).setStatus(EventStatus.CANCELLED);
        verify(bookingService).unregister(booking1);
        verify(bookingService).unregister(booking2);
    }

}
