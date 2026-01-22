package uk.gov.cslearning.record.domain;

import uk.gov.cslearning.record.domain.record.LearnerRecordType;
import uk.gov.cslearning.record.domain.record.event.LearnerRecordEventType;

import java.util.List;
import java.util.Map;

public class LearnerRecordTypeMapping {

    private final Map<String, LearnerRecordType> typeMap;
    private final Map<String, Map<String, LearnerRecordEventType>> eventTypeMap;

    public LearnerRecordTypeMapping(Map<String, LearnerRecordType> typeMap, Map<String, Map<String, LearnerRecordEventType>> eventTypeMap) {
        this.typeMap = typeMap;
        this.eventTypeMap = eventTypeMap;
    }

    public List<LearnerRecordEventType> getAllEventTypes() {
        return eventTypeMap.values().stream().flatMap(map -> map.values().stream()).toList();
    }

    public LearnerRecordType getType(String name) {
        LearnerRecordType type = this.typeMap.get(name);
        if (type == null) {
            throw new RuntimeException(String.format("Learner record type %s is invalid", name));
        }
        return type;
    }

    public LearnerRecordEventType getEventType(String learnerRecordEventType) {
        return eventTypeMap.values().stream().flatMap(e -> e.values().stream())
                .filter(e -> e.getEventType().equals(learnerRecordEventType))
                .findFirst().orElseThrow(() -> new RuntimeException(String.format("Learner record eventUid type %s is invalid", learnerRecordEventType)));
    }

    public LearnerRecordEventType getEventType(String learnerRecordType, String learnerRecordEventType) {
        Map<String, LearnerRecordEventType> map = eventTypeMap.get(learnerRecordType);
        if (map != null) {
            LearnerRecordEventType eventType = map.get(learnerRecordEventType);
            if (eventType != null) {
                return eventType;
            }
        }
        throw new RuntimeException(String.format("Learner record eventUid type with record type %s and eventUid type %s is invalid", learnerRecordType, learnerRecordEventType));
    }

}
