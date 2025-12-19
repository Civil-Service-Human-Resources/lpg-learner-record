package uk.gov.cslearning.record.service;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.domain.BookingStatus;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.domain.factory.EventFactory;
import uk.gov.cslearning.record.dto.EventDto;
import uk.gov.cslearning.record.dto.EventStatus;
import uk.gov.cslearning.record.dto.EventStatusDto;
import uk.gov.cslearning.record.dto.factory.EventDtoFactory;
import uk.gov.cslearning.record.exception.EventNotFoundException;
import uk.gov.cslearning.record.repository.EventRepository;
import uk.gov.cslearning.record.util.IUtilService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DefaultEventService implements EventService {
    private final IUtilService utilService;
    private final EventRepository eventRepository;
    private final EventFactory eventFactory;
    private final EventDtoFactory eventDtoFactory;

    public DefaultEventService(IUtilService utilService, EventRepository eventRepository,
                               EventFactory eventFactory, EventDtoFactory eventDtoFactory) {
        this.utilService = utilService;
        this.eventRepository = eventRepository;
        this.eventFactory = eventFactory;
        this.eventDtoFactory = eventDtoFactory;
    }

    @Override
    public Event getEventAndCreateIfMissing(String eventUid) {
        return eventRepository.findByUid(eventUid)
                .orElseGet(() -> eventRepository.save(eventFactory.create(eventUid)));
    }

    @Override
    public EventDto updateStatus(String eventUid, EventStatusDto eventStatus) {
        Event event = eventRepository.findByUid(eventUid)
                .map(e -> {
                    e.setStatus(eventStatus.getStatus());
                    if (eventStatus.getStatus().equals(EventStatus.CANCELLED)) {
                        e.cancel(eventStatus.getCancellationReason(), utilService.getNowInstant());
                    }
                    return eventRepository.save(e);
                })
                .orElseThrow(() -> new EventNotFoundException(eventUid));
        return eventDtoFactory.create(event);
    }

    private Integer getActiveBookingsCount(Integer eventId) {
        return eventRepository.countByBookings_StatusInAndIdEquals(List.of(BookingStatus.CONFIRMED,
                BookingStatus.REQUESTED), eventId);
    }

    @Override
    public EventDto findByUid(String uid, boolean getBookingCount) {
        return eventRepository.findByUid(uid)
                .map(e -> {
                    EventDto dto = this.eventDtoFactory.create(e);
                    if (getBookingCount) {
                        dto.setActiveBookingCount(getActiveBookingsCount(e.getId()));
                    }
                    return dto;
                })
                .orElse(null);
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
