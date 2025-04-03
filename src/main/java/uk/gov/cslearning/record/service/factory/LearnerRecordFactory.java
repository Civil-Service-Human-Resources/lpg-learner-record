package uk.gov.cslearning.record.service.factory;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.domain.record.LearnerRecord;
import uk.gov.cslearning.record.domain.record.LearnerRecordType;
import uk.gov.cslearning.record.domain.record.event.LearnerRecordEvent;
import uk.gov.cslearning.record.dto.record.CreateLearnerRecordDto;
import uk.gov.cslearning.record.dto.record.LearnerRecordDto;
import uk.gov.cslearning.record.dto.record.LearnerRecordEventDto;
import uk.gov.cslearning.record.dto.record.LearnerRecordTypeDto;
import uk.gov.cslearning.record.service.LookupValueService;
import uk.gov.cslearning.record.util.UtilService;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class LearnerRecordFactory {
    private final LearnerRecordEventFactory learnerRecordEventFactory;
    private final LookupValueFactory lookupValueFactory;
    private final LookupValueService lookupValueService;
    private final UtilService utilService;

    public LearnerRecordFactory(LearnerRecordEventFactory learnerRecordEventFactory,
                                LookupValueFactory lookupValueFactory, LookupValueService lookupValueService, UtilService utilService) {
        this.learnerRecordEventFactory = learnerRecordEventFactory;
        this.lookupValueFactory = lookupValueFactory;
        this.lookupValueService = lookupValueService;
        this.utilService = utilService;
    }

    public LearnerRecordDto createLearnerRecordDto(LearnerRecord learnerRecord) {
        LearnerRecordTypeDto typeDto = lookupValueFactory.createLearnerRecordTypeDto(learnerRecord.getLearnerRecordType());
        return new LearnerRecordDto(learnerRecord.getId(), learnerRecord.getLearnerRecordUid(), typeDto, learnerRecord.getParentId(),
                learnerRecord.getResourceId(), learnerRecord.getCreatedTimestamp(), learnerRecord.getLearnerId());
    }

    public LearnerRecordDto createLearnerRecordDto(LearnerRecord learnerRecord, boolean includeChildren, boolean includeEvents) {
        LearnerRecordDto dto = createLearnerRecordDto(learnerRecord);
        if (includeChildren) {
            List<LearnerRecordDto> children = learnerRecord.getChildRecords()
                    .stream().map(this::createLearnerRecordDto).toList();
            dto.setChildren(children);
        }
        if (includeEvents) {
            List<LearnerRecordEventDto> events = learnerRecord.getEvents()
                    .stream().map(learnerRecordEventFactory::createDto).toList();
            dto.setEvents(events);
        }
        return dto;
    }

    public LearnerRecord createLearnerRecord(LearnerRecord parent, CreateLearnerRecordDto dto) {
        LearnerRecord record = createLearnerRecord(dto);
        record.setParentRecord(parent);
        return record;
    }

    public LearnerRecord createLearnerRecord(CreateLearnerRecordDto dto) {
        Instant createdTimestamp = dto.getCreatedTimestamp() == null ? utilService.getNowInstant() : utilService.localDateTimeToInstant(dto.getCreatedTimestamp());
        LearnerRecordType learnerRecordType = lookupValueService.getLearnerRecordType(dto.getRecordType());
        LearnerRecord record = new LearnerRecord(learnerRecordType, UUID.randomUUID().toString(), dto.getLearnerId(),
                dto.getResourceId(), createdTimestamp);
        List<LearnerRecordEvent> events = dto.getEvents().stream()
                .map(e -> learnerRecordEventFactory.createEvent(record, e)).toList();
        record.setEvents(events);
        List<LearnerRecord> children = dto.getChildren().stream()
                .map(this::createLearnerRecord).toList();
        record.setChildRecords(children);
        return record;
    }

}
