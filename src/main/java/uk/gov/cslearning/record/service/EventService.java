package uk.gov.cslearning.record.service;

import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.dto.EventDto;
import uk.gov.cslearning.record.dto.EventStatusDto;

public interface EventService {

    @Transactional
    void cancelEvent(String eventId);

    @Transactional(readOnly = true)
    Event getEvent(String eventUid, String path);

    @Transactional
    EventDto updateStatus(String eventUid, EventStatusDto eventStatus);
}
