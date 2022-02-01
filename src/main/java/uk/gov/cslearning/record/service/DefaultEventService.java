package uk.gov.cslearning.record.service;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.domain.factory.EventFactory;
import uk.gov.cslearning.record.dto.*;
import uk.gov.cslearning.record.dto.factory.EventDtoFactory;
import uk.gov.cslearning.record.exception.EventNotFoundException;
import uk.gov.cslearning.record.repository.EventRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        if (!eventRepository.findByUid(eventUid).isPresent()) {
            eventRepository.save(eventFactory.create(path));
        }
    }

    @Override
    public Event getEvent(String eventUid, String path) {
        createEventIfNotPresent(eventUid, path);
        return eventRepository.findByUid(eventUid).get();
    }

    @Override
    public Optional<EventDto> updateStatus(String eventUid, EventStatusDto eventStatus) {
        Event event = eventRepository.findByUid(eventUid).orElseThrow(() -> new EventNotFoundException(eventUid));

        EventDto eventDto = eventDtoFactory.create(event);
        eventDto.setStatus(eventStatus.getStatus());

        if (eventStatus.getStatus().equals(EventStatus.CANCELLED)) {
            eventDto.setCancellationReason(CancellationReason.valueOf(eventStatus.getCancellationReason()));
            event.getBookings().forEach(booking -> bookingService.unregister(booking, eventDto.getCancellationReason().getValue()));
        }

        return Optional.of(eventDtoFactory.create(eventRepository.save(eventFactory.create(eventDto))));
    }

    private Integer getActiveBookingsCount(Integer eventId) {
        Collection<BookingStatus> queryStatuses = new ArrayList<>();
        queryStatuses.add(BookingStatus.CONFIRMED);
        queryStatuses.add(BookingStatus.REQUESTED);
        return eventRepository.countByBookings_StatusInAndIdEquals(queryStatuses, eventId);
    }

    @Override
    public EventDto findByUid(String uid) {
        return findByUid(uid, false);
    }

    @Override
    public EventDto findByUid(String uid, boolean getBookingCount) {
        EventDto event = eventRepository.findByUid(uid)
                .map(eventDtoFactory::create)
                .orElse(null);

        if (event != null) {
            if (getBookingCount) {
                event.setActiveBookingCount(getActiveBookingsCount(event.getId()));
            }
        }
        return event;
    }

    @Override
    public EventDto create(EventDto eventDto) {
        return eventDtoFactory.create(eventRepository.save(eventFactory.create(eventDto)));
    }

    @Override
    public List<EventDto> getEvents(List<String> eventUids, boolean getBookingCount) {
         return eventRepository.findByUidIn(eventUids).stream()
                 .map(eventDtoFactory::create)
                 .peek(eventDto -> {
                     if (getBookingCount) {
                         eventDto.setActiveBookingCount(getActiveBookingsCount(eventDto.getId()));
                     }
                 })
                 .collect(Collectors.toList());
    }
}
