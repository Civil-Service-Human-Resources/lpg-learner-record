package uk.gov.cslearning.record.service;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.exception.EventNotFoundException;
import uk.gov.cslearning.record.domain.factory.EventFactory;
import uk.gov.cslearning.record.repository.EventRepository;

@Service
public class DefaultEventService implements EventService {
    private final EventRepository eventRepository;
    private final BookingService bookingService;
    private final EventFactory eventFactory;

    public DefaultEventService(EventRepository eventRepository, BookingService bookingService, EventFactory eventFactory) {
        this.eventRepository = eventRepository;
        this.bookingService = bookingService;
        this.eventFactory = eventFactory;
    }


    @Override
    public void cancelEvent(String eventId) {
        Event event = eventRepository.findByEventUid(eventId).orElseThrow(() -> new EventNotFoundException(eventId));
        event.getBookings().forEach(bookingService::unregister);
        eventRepository.delete(event);
    }

    private void createEventIfNotPresent(String eventUid, String path) {
        if(!eventRepository.findByEventUid(eventUid).isPresent()){
            eventRepository.save(eventFactory.create(path));
        }
    }

    @Override
    public Event getEvent(String eventUid, String path){
        createEventIfNotPresent(eventUid, path);
        return eventRepository.findByEventUid(eventUid).get();
    }
}
