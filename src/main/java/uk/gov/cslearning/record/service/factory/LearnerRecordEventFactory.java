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
import uk.gov.cslearning.record.service.LookupValueService;
import uk.gov.cslearning.record.util.UtilService;

import java.time.Instant;
import java.util.List;

@Service
public class LearnerRecordEventFactory {

    private final LookupValueService lookupValueService;
    private final UtilService utilService;

    public LearnerRecordEventFactory(LookupValueService lookupValueService, UtilService utilService) {
        this.lookupValueService = lookupValueService;
        this.utilService = utilService;
    }

    public LearnerRecordEvent createEvent(LearnerRecord record, CreateLearnerRecordEventDto dto) {
        Instant creationTimestamp = dto.getEventTimestamp() == null ? utilService.getNowInstant() : dto.getEventTimestamp();
        LearnerRecordEventType learnerRecordEventType = lookupValueService.getLearnerRecordEventType(record.getLearnerRecordType().getId(), dto.getEventType());
        LearnerRecordEventSource learnerRecordEventSource = lookupValueService.getLearnerRecordSource(dto.getEventSource());
        LearnerRecordEvent event = new LearnerRecordEvent(record, learnerRecordEventType, learnerRecordEventSource, creationTimestamp);
        if (dto.getEventTimestamp() != null) {
            event.setEventTimestamp(dto.getEventTimestamp());
        }
        return event;
    }

    public Page<LearnerRecordEventDto> createDtos(Pageable pageable, Page<LearnerRecordEvent> events) {
        List<LearnerRecordEventDto> eventDtos = events.map(this::createDto).stream().toList();
        return new PageImpl<>(eventDtos, pageable, eventDtos.size());
    }

    public LearnerRecordEventDto createDto(LearnerRecordEvent event) {
        return new LearnerRecordEventDto(event.getId(), event.getLearnerRecord().getId(),
                event.getEventType().getId(), event.getEventSource().getId(), event.getEventTimestamp());
    }
}
