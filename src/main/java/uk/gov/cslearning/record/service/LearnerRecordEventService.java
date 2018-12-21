package uk.gov.cslearning.record.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.dto.LearnerRecordEvent;
import uk.gov.cslearning.record.dto.factory.LearnerRecordEventFactory;
import uk.gov.cslearning.record.repository.CourseRecordRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LearnerRecordEventService {
    private final CourseRecordRepository courseRecordRepository;
    private final LearnerRecordEventFactory learnerRecordEventFactory;

    public LearnerRecordEventService(CourseRecordRepository courseRecordRepository, LearnerRecordEventFactory learnerRecordEventFactory) {
        this.courseRecordRepository = courseRecordRepository;
        this.learnerRecordEventFactory = learnerRecordEventFactory;
    }

    @Transactional(readOnly = true)
    public List<LearnerRecordEvent> listEvents() {
        List<CourseRecord> courseRecords = courseRecordRepository.listEventRecords();

        Map<String, LearnerRecordEvent> events = new HashMap<>();

        for (CourseRecord courseRecord : courseRecords) {
            for (ModuleRecord moduleRecord : courseRecord.getModuleRecords()) {
                String key = String.format("%s-%s", courseRecord.getUserId(), moduleRecord.getModuleId());

                events.put(key, learnerRecordEventFactory.create(courseRecord, moduleRecord));
            }
        }

        return Collections.unmodifiableList(new ArrayList<>(events.values()));
    }
}
