package uk.gov.cslearning.record.domain;

import uk.gov.cslearning.record.domain.record.LearnerRecordType;
import uk.gov.cslearning.record.domain.record.event.LearnerRecordEventType;

import java.util.List;
import java.util.Map;

public class LearnerRecordTypeMapping {

    private final Map<Integer, LearnerRecordType> typeMap;
    private final Map<Integer, Map<Integer, LearnerRecordEventType>> eventTypeMap;

    public LearnerRecordTypeMapping(Map<Integer, LearnerRecordType> typeMap, Map<Integer, Map<Integer, LearnerRecordEventType>> eventTypeMap) {
        this.typeMap = typeMap;
        this.eventTypeMap = eventTypeMap;
    }

    public List<LearnerRecordType> getAllTypes() {
        return typeMap.values().stream().toList();
    }

    public List<LearnerRecordEventType> getAllEventTypes() {
        return eventTypeMap.values().stream().flatMap(map -> map.values().stream()).toList();
    }

    public LearnerRecordType getType(Integer id) {
        LearnerRecordType type = this.typeMap.get(id);
        if (type == null) {
            throw new RuntimeException(String.format("Learner record type with id %s is invalid", id));
        }
        return type;
    }

    public LearnerRecordEventType getEventType(Integer learnerRecordTypeId, Integer learnerRecordEventTypeId) {
        Map<Integer, LearnerRecordEventType> map = eventTypeMap.get(learnerRecordTypeId);
        if (map != null) {
            LearnerRecordEventType eventType = map.get(learnerRecordEventTypeId);
            if (eventType != null) {
                return eventType;
            }
        }
        throw new RuntimeException(String.format("Learner record event type with type id %s and event type id %s is invalid", learnerRecordTypeId, learnerRecordEventTypeId));
    }

}
