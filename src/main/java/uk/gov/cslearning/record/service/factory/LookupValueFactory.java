package uk.gov.cslearning.record.service.factory;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.domain.record.LearnerRecordType;
import uk.gov.cslearning.record.domain.record.event.LearnerRecordEventType;
import uk.gov.cslearning.record.dto.record.LearnerRecordEventTypeDto;
import uk.gov.cslearning.record.dto.record.LearnerRecordTypeDto;

@Service
public class LookupValueFactory {

    public LearnerRecordTypeDto createLearnerRecordTypeDto(LearnerRecordType type) {
        return new LearnerRecordTypeDto(type.getId(), type.getRecordType());
    }

    public LearnerRecordEventTypeDto createLearnerRecordEventTypeDto(LearnerRecordEventType eventType) {
        return new LearnerRecordEventTypeDto(eventType.getId(), eventType.getEventType(), eventType.getDescription());
    }

}
