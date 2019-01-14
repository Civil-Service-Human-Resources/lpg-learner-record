package uk.gov.cslearning.record.service;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.domain.factory.EventFactory;
import uk.gov.cslearning.record.dto.CancellationReason;
import uk.gov.cslearning.record.dto.EventDto;
import uk.gov.cslearning.record.dto.EventStatus;
import uk.gov.cslearning.record.dto.EventStatusDto;
import uk.gov.cslearning.record.dto.factory.EventDtoFactory;
import uk.gov.cslearning.record.exception.EventNotFoundException;
import uk.gov.cslearning.record.repository.EventRepository;

import java.util.Optional;

@Service
public class DefaultEventService implements EventService {
    private final EventRepository eventRepository;
    private final BookingService bookingService;
    private final EventFactory eventFactory;
    private final EventDtoFactory eventDtoFactory;

    public DefaultEventService(EventRepository eventRepository, BookingService bookingService, EventFactory eventFactory, EventDtoFactory eventDtoFactory) {
        this.eventRepository = eventRepository;
        this.bookingService = bookingService;
        this.eventFactory = eventFactory;
        this.eventDtoFactory = eventDtoFactory;
    }

    private void createEventIfNotPresent(String eventUid, String path) {
        if(!eventRepository.findByUid(eventUid).isPresent()){
            eventRepository.save(eventFactory.create(path));
        }
    }

    @Override
    public Event getEvent(String eventUid, String path){
        createEventIfNotPresent(eventUid, path);
        return eventRepository.findByUid(eventUid).get();
    }

    @Override
    public Optional<EventDto> updateStatus(String eventUid, EventStatusDto eventStatus) {
        Event event = eventRepository.findByUid(eventUid).orElseThrow(() -> new EventNotFoundException(eventUid));

        if (eventStatus.getStatus().equals(EventStatus.CANCELLED)) {
            event.getBookings().forEach(booking -> bookingService.unregister(booking, eventStatus.getCancellationReason()));
        }

        EventDto eventDto = eventDtoFactory.create(event);
        eventDto.setStatus(eventStatus.getStatus());

        eventDto.setCancellationReason(CancellationReason.forValue(eventStatus.getCancellationReason()));

        return Optional.of(eventDtoFactory.create(eventRepository.save(eventFactory.create(eventDto))));
    }

    @Override
    public Optional<EventDto> findByUid(String uid) {
        EventDto event = eventRepository.findByUid(uid)
                .map(eventDtoFactory::create)
                .orElse(null);

        return Optional.ofNullable(event);
    }

    @Override
    public EventDto create(EventDto eventDto){
        return eventDtoFactory.create(eventRepository.save(eventFactory.create(eventDto)));
    }
}
