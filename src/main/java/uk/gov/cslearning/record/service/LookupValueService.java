package uk.gov.cslearning.record.service;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.domain.LearnerRecordTypeMapping;
import uk.gov.cslearning.record.domain.record.LearnerRecordType;
import uk.gov.cslearning.record.domain.record.event.LearnerRecordEventSource;
import uk.gov.cslearning.record.domain.record.event.LearnerRecordEventType;
import uk.gov.cslearning.record.dto.record.LearnerRecordEventTypeDto;
import uk.gov.cslearning.record.service.factory.LookupValueFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class LookupValueService {

    private final LearnerRecordTypeMapping learnerRecordTypeMap;
    private final Map<String, LearnerRecordEventSource> learnerRecordEventSourceMap;
    private final LookupValueFactory factory;

    public LookupValueService(LearnerRecordTypeMapping learnerRecordTypeMap, Map<String, LearnerRecordEventSource> learnerRecordEventSourceMap, LookupValueFactory factory) {
        this.learnerRecordTypeMap = learnerRecordTypeMap;
        this.learnerRecordEventSourceMap = learnerRecordEventSourceMap;
        this.factory = factory;
    }

    public List<LearnerRecordEventTypeDto> getLearnerRecordEventTypes() {
        return learnerRecordTypeMap.getAllEventTypes().stream().map(t -> factory.createLearnerRecordEventTypeDto(t, true, true))
                .sorted(Comparator.comparing(LearnerRecordEventTypeDto::getId))
                .toList();
    }

    public LearnerRecordEventSource getLearnerRecordSource(String uid) {
        LearnerRecordEventSource source = this.learnerRecordEventSourceMap.get(uid);
        if (source == null) {
            throw new RuntimeException(String.format("Learner record event source with uid %s is invalid", uid));
        }
        return source;
    }

    public LearnerRecordType getLearnerRecordType(String name) {
        return learnerRecordTypeMap.getType(name);
    }

    public LearnerRecordEventType getLearnerRecordEventType(String learnerRecordType, String learnerRecordEventType) {
        return learnerRecordTypeMap.getEventType(learnerRecordType, learnerRecordEventType);
    }
}
