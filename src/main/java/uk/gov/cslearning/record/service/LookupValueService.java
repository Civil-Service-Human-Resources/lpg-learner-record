package uk.gov.cslearning.record.service;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.domain.LearnerRecordTypeMapping;
import uk.gov.cslearning.record.domain.record.LearnerRecordType;
import uk.gov.cslearning.record.domain.record.event.LearnerRecordEventSource;
import uk.gov.cslearning.record.domain.record.event.LearnerRecordEventType;
import uk.gov.cslearning.record.dto.record.LearnerRecordEventTypeDto;
import uk.gov.cslearning.record.service.factory.LookupValueFactory;

import java.util.List;
import java.util.Map;

@Service
public class LookupValueService {

    private final LearnerRecordTypeMapping learnerRecordTypeMap;
    private final Map<Integer, LearnerRecordEventSource> learnerRecordEventSourceMap;
    private final LookupValueFactory factory;

    public LookupValueService(LearnerRecordTypeMapping learnerRecordTypeMap, Map<Integer, LearnerRecordEventSource> learnerRecordEventSourceMap, LookupValueFactory factory) {
        this.learnerRecordTypeMap = learnerRecordTypeMap;
        this.learnerRecordEventSourceMap = learnerRecordEventSourceMap;
        this.factory = factory;
    }

    public List<LearnerRecordEventTypeDto> getLearnerRecordEventTypes() {
        return learnerRecordTypeMap.getAllEventTypes().stream().map(t -> factory.createLearnerRecordEventTypeDto(t, true, true)).toList();
    }

    public LearnerRecordEventSource getLearnerRecordSource(Integer id) {
        LearnerRecordEventSource source = this.learnerRecordEventSourceMap.get(id);
        if (source == null) {
            throw new RuntimeException(String.format("Learner record event source with id %s is invalid", id));
        }
        return source;
    }

    public LearnerRecordType getLearnerRecordType(Integer id) {
        return learnerRecordTypeMap.getType(id);
    }

    public LearnerRecordEventType getLearnerRecordEventType(Integer learnerRecordTypeId, Integer learnerRecordEventTypeId) {
        return learnerRecordTypeMap.getEventType(learnerRecordTypeId, learnerRecordEventTypeId);
    }
}
