package uk.gov.cslearning.record.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.domain.LearnerRecordTypeMapping;
import uk.gov.cslearning.record.domain.record.LearnerRecordType;
import uk.gov.cslearning.record.domain.record.event.LearnerRecordEventSource;
import uk.gov.cslearning.record.domain.record.event.LearnerRecordEventType;
import uk.gov.cslearning.record.repository.LearnerRecordEventSourceRepository;
import uk.gov.cslearning.record.repository.LearnerRecordTypeRepository;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class LookupValueConfig {

    private final LearnerRecordTypeRepository learnerRecordTypeRepository;
    private final LearnerRecordEventSourceRepository learnerRecordEventSourceRepository;

    public LookupValueConfig(LearnerRecordTypeRepository learnerRecordTypeRepository, LearnerRecordEventSourceRepository learnerRecordEventSourceRepository) {
        this.learnerRecordTypeRepository = learnerRecordTypeRepository;
        this.learnerRecordEventSourceRepository = learnerRecordEventSourceRepository;
    }

    @Bean
    @Transactional
    public LearnerRecordTypeMapping learnerRecordTypeMap() {
        Map<String, LearnerRecordType> typeMap = new HashMap<>();
        Map<String, Map<String, LearnerRecordEventType>> eventTypeMap = new HashMap<>();

        for (LearnerRecordType type : learnerRecordTypeRepository.findAll()) {
            Map<String, LearnerRecordEventType> eventTypes = new HashMap<>();
            typeMap.put(type.getRecordType(), type);
            for (LearnerRecordEventType eventType : type.getEventTypes()) {
                eventTypes.put(eventType.getEventType(), eventType);
            }
            eventTypeMap.put(type.getRecordType(), eventTypes);
        }

        return new LearnerRecordTypeMapping(typeMap, eventTypeMap);
    }

    @Bean
    public Map<String, LearnerRecordEventSource> learnerRecordEventSourceMap() {
        Map<String, LearnerRecordEventSource> map = new HashMap<>();
        for (LearnerRecordEventSource source : learnerRecordEventSourceRepository.findAll()) {
            map.put(source.getUid(), source);
        }
        return map;
    }

}
