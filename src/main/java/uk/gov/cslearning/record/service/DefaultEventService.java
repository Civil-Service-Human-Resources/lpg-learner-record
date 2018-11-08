package uk.gov.cslearning.record.service;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.exception.EventNotFoundException;
import uk.gov.cslearning.record.repository.EventRepository;

@Service
public class DefaultEventService implements EventService {
    private final EventRepository eventRepository;
    private final BookingService bookingService;

    public DefaultEventService(EventRepository eventRepository, BookingService bookingService) {
        this.eventRepository = eventRepository;
        this.bookingService = bookingService;
    }

    @Override
    public void cancelEvent(String eventId) {
        Event event = eventRepository.findByCatalogueId(eventId).orElseThrow(() -> new EventNotFoundException(eventId));

        event.getBookings().forEach(booking -> bookingService.unregister(booking.getId()));

        eventRepository.delete(event);
    }
}
