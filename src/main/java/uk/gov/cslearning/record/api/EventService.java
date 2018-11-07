package uk.gov.cslearning.record.api;

import org.springframework.transaction.annotation.Transactional;

public interface EventService {

    @Transactional
    void cancelEvent(String eventId);
}
