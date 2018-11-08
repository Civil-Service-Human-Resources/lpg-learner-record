package uk.gov.cslearning.record.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.record.domain.Booking;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.exception.EventNotFoundException;
import uk.gov.cslearning.record.repository.EventRepository;
import uk.gov.cslearning.record.service.BookingService;
import uk.gov.cslearning.record.service.DefaultEventService;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultEventServiceTest {
    @Mock
    private EventRepository eventRepository;

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private DefaultEventService eventService;

    @Test
    public void shouldUnregisterBookingsAndDeleteEvent() {
        String eventId = "event-id";
        Event event = new Event();
        Booking booking1 = new Booking();
        booking1.setId(10);
        Booking booking2 = new Booking();
        booking2.setId(20);
        event.setBookings(Arrays.asList(booking1, booking2));

        when(eventRepository.findByCatalogueId(eventId)).thenReturn(Optional.of(event));

        eventService.cancelEvent(eventId);

        verify(bookingService).unregister(10);
        verify(bookingService).unregister(20);
        verify(eventRepository).delete(event);
    }

    @Test
    public void shouldThrowEventNotFoundException() {
        String eventId = "event-id";

        when(eventRepository.findByCatalogueId(eventId)).thenReturn(Optional.empty());

        try {
            eventService.cancelEvent(eventId);
            fail("Expected EventNotFoundException");
        } catch (EventNotFoundException e) {
            assertEquals("Event does not exist with catalogue id: " + eventId, e.getMessage());
        }
    }
}