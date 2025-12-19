package uk.gov.cslearning.record.service;

import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.dto.EventDto;
import uk.gov.cslearning.record.dto.EventStatusDto;

import java.util.List;

public interface EventService {

    @Transactional(readOnly = true)
    Event getEventAndCreateIfMissing(String eventUid);

    @Transactional
    EventDto updateStatus(String eventUid, EventStatusDto eventStatus);

    @Transactional(readOnly = true)
    EventDto findByUid(String eventUid, boolean getBookingCount);

    @Transactional
    EventDto create(EventDto eventDto);

    @Transactional
    List<EventDto> getEvents(List<String> eventUids, boolean getBookingCount);
}
