package uk.gov.cslearning.record.service.factory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.domain.record.LearnerRecord;
import uk.gov.cslearning.record.domain.record.event.LearnerRecordEvent;
import uk.gov.cslearning.record.domain.record.event.LearnerRecordEventSource;
import uk.gov.cslearning.record.domain.record.event.LearnerRecordEventType;
import uk.gov.cslearning.record.dto.record.CreateLearnerRecordEventDto;
import uk.gov.cslearning.record.dto.record.LearnerRecordEventDto;
import uk.gov.cslearning.record.dto.record.LearnerRecordEventSourceDto;
import uk.gov.cslearning.record.dto.record.LearnerRecordEventTypeDto;
import uk.gov.cslearning.record.service.LookupValueService;
import uk.gov.cslearning.record.util.UtilService;

import java.time.Instant;
import java.util.List;

@Service
public class LearnerRecordEventFactory {

    private final LookupValueService lookupValueService;
    private final LookupValueFactory lookupValueFactory;
    private final UtilService utilService;

    public LearnerRecordEventFactory(LookupValueService lookupValueService, LookupValueFactory lookupValueFactory, UtilService utilService) {
        this.lookupValueService = lookupValueService;
        this.lookupValueFactory = lookupValueFactory;
        this.utilService = utilService;
    }

    public LearnerRecordEvent createEvent(LearnerRecord record, CreateLearnerRecordEventDto dto) {
        Instant creationTimestamp = dto.getEventTimestamp() == null ? utilService.getNowInstant() : utilService.localDateTimeToInstant(dto.getEventTimestamp());
        LearnerRecordEventType learnerRecordEventType = lookupValueService.getLearnerRecordEventType(record.getLearnerRecordType().getRecordType(), dto.getEventType());
        LearnerRecordEventSource learnerRecordEventSource = lookupValueService.getLearnerRecordSource(dto.getEventSource());
        LearnerRecordEvent event = new LearnerRecordEvent(record, learnerRecordEventType, learnerRecordEventSource, creationTimestamp);
        if (dto.getEventTimestamp() != null) {
            event.setEventTimestamp(utilService.localDateTimeToInstant(dto.getEventTimestamp()));
        }
        return event;
    }

    public Page<LearnerRecordEventDto> createDtos(Pageable pageable, Page<LearnerRecordEvent> events) {
        List<LearnerRecordEventDto> eventDtos = events.map(this::createDto).stream().toList();
        return new PageImpl<>(eventDtos, pageable, eventDtos.size());
    }

    public LearnerRecordEventDto createDto(LearnerRecordEvent event) {
        LearnerRecordEventTypeDto eventTypeDto = lookupValueFactory.createLearnerRecordEventTypeDto(event.getEventType());
        LearnerRecordEventSourceDto eventSourceDto = lookupValueFactory.createLearnerRecordSourceDto(event.getEventSource());
        LearnerRecord record = event.getLearnerRecord();
        return new LearnerRecordEventDto(event.getLearnerRecord().getId(), record.getResourceId(), record.getLearnerId(), eventTypeDto, eventSourceDto,
                event.getEventTimestamp());
    }
}
