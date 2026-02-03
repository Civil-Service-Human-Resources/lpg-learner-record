package uk.gov.cslearning.record.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.api.input.FindEventParams;
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
public class EventService {
    private final IUtilService utilService;
    private final EventRepository eventRepository;
    private final EventFactory eventFactory;
    private final EventDtoFactory eventDtoFactory;

    public EventService(IUtilService utilService, EventRepository eventRepository,
                        EventFactory eventFactory, EventDtoFactory eventDtoFactory) {
        this.utilService = utilService;
        this.eventRepository = eventRepository;
        this.eventFactory = eventFactory;
        this.eventDtoFactory = eventDtoFactory;
    }

    @Transactional(readOnly = true)
    public Event getEventAndCreateIfMissing(String eventUid) {
        return eventRepository.findByUid(eventUid)
                .orElseGet(() -> eventRepository.save(eventFactory.create(eventUid)));
    }

    @Transactional
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

    @Transactional(readOnly = true)
    public EventDto findByUid(String uid, FindEventParams params) {
        return eventRepository.findByUid(uid)
                .map(e -> this.eventDtoFactory.create(e, params))
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public EventDto findByUid(String uid) {
        return findByUid(uid, new FindEventParams());
    }

    @Transactional
    public EventDto create(EventDto eventDto) {
        return eventDtoFactory.create(eventRepository.save(eventFactory.create(eventDto)));
    }

    @Transactional
    public List<EventDto> getEvents(List<String> eventUids, boolean getBookingCount) {
        return eventRepository.findByUidIn(eventUids).stream()
                .map(e -> eventDtoFactory.create(e, new FindEventParams(getBookingCount, false, false)))
                .collect(Collectors.toList());
    }
}
