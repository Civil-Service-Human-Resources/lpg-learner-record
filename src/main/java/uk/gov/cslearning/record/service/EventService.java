package uk.gov.cslearning.record.service;

import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.dto.EventDto;
import uk.gov.cslearning.record.dto.EventStatusDto;

import java.util.List;
import java.util.Optional;

public interface EventService {

    @Transactional(readOnly = true)
    Event getEvent(String eventUid, String path);

    @Transactional
    Optional<EventDto> updateStatus(String eventUid, EventStatusDto eventStatus);

    @Transactional(readOnly = true)
    EventDto findByUid(String eventUid, boolean getBookingCount);

    @Transactional(readOnly = true)
    EventDto findByUid(String uid);

    @Transactional
    EventDto create(EventDto eventDto);

    @Transactional
    List<EventDto> getEvents(String[] eventUids, boolean getBookingCount);
}
