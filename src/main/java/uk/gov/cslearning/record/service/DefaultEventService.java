package uk.gov.cslearning.record.service;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.domain.factory.EventFactory;
import uk.gov.cslearning.record.repository.EventRepository;

@Service
public class DefaultEventService implements EventService {

    private final EventRepository eventRepository;
    private final EventFactory eventFactory;

    public DefaultEventService(EventRepository eventRepository, EventFactory eventFactory){
        this.eventRepository = eventRepository;
        this.eventFactory = eventFactory;
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
