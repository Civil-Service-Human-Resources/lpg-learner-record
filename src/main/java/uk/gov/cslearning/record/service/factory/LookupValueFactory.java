package uk.gov.cslearning.record.service.factory;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.domain.record.LearnerRecordType;
import uk.gov.cslearning.record.domain.record.event.LearnerRecordEventSource;
import uk.gov.cslearning.record.domain.record.event.LearnerRecordEventType;
import uk.gov.cslearning.record.dto.record.LearnerRecordEventSourceDto;
import uk.gov.cslearning.record.dto.record.LearnerRecordEventTypeDto;
import uk.gov.cslearning.record.dto.record.LearnerRecordTypeDto;

@Service
public class LookupValueFactory {

    public LearnerRecordTypeDto createLearnerRecordTypeDto(LearnerRecordType type) {
        return new LearnerRecordTypeDto(type.getId(), type.getRecordType());
    }

    public LearnerRecordTypeDto createLearnerRecordTypeDto(LearnerRecordType type, boolean includeEventTypes) {
        LearnerRecordTypeDto dto = createLearnerRecordTypeDto(type);
        if (includeEventTypes) {
            dto.setValidEventTypes(type.getEventTypes().stream()
                    .map(e -> this.createLearnerRecordEventTypeDto(e, true, false)).toList());
        }
        return dto;
    }

    public LearnerRecordEventTypeDto createLearnerRecordEventTypeDto(LearnerRecordEventType eventType) {
        return new LearnerRecordEventTypeDto(eventType.getId(), eventType.getEventType());
    }

    public LearnerRecordEventTypeDto createLearnerRecordEventTypeDto(LearnerRecordEventType eventType, boolean includeDescription, boolean includeRecordType) {
        LearnerRecordEventTypeDto dto = createLearnerRecordEventTypeDto(eventType);
        if (includeDescription) {
            dto.setDescription(eventType.getDescription());
        }
        if (includeRecordType) {
            LearnerRecordTypeDto typeDto = createLearnerRecordTypeDto(eventType.getRecordType());
            dto.setLearnerRecordType(typeDto);
        }
        return dto;
    }

    public LearnerRecordEventSourceDto createLearnerRecordSourceDto(LearnerRecordEventSource eventSource) {
        return new LearnerRecordEventSourceDto(eventSource.getId(), eventSource.getSource());
    }
}
