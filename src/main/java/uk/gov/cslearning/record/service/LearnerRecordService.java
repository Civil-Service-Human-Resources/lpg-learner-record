package uk.gov.cslearning.record.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.api.record.LearnerRecordEventQuery;
import uk.gov.cslearning.record.api.record.LearnerRecordQuery;
import uk.gov.cslearning.record.domain.record.LearnerRecord;
import uk.gov.cslearning.record.domain.record.event.LearnerRecordEvent;
import uk.gov.cslearning.record.dto.record.CreateLearnerRecordDto;
import uk.gov.cslearning.record.dto.record.CreateLearnerRecordEventDto;
import uk.gov.cslearning.record.dto.record.LearnerRecordDto;
import uk.gov.cslearning.record.dto.record.LearnerRecordEventDto;
import uk.gov.cslearning.record.exception.LearnerRecordNotFoundException;
import uk.gov.cslearning.record.repository.LearnerRecordEventRepository;
import uk.gov.cslearning.record.repository.LearnerRecordRepository;
import uk.gov.cslearning.record.service.factory.LearnerRecordEventFactory;
import uk.gov.cslearning.record.service.factory.LearnerRecordFactory;
import uk.gov.cslearning.record.util.IUtilService;

import java.util.List;

@Service
public class LearnerRecordService {

    private final IUtilService utilService;
    private final LearnerRecordRepository learnerRecordRepository;
    private final LearnerRecordEventRepository learnerRecordEventepository;
    private final LearnerRecordFactory learnerRecordFactory;
    private final LearnerRecordEventFactory learnerRecordEventFactory;

    public LearnerRecordService(IUtilService utilService, LearnerRecordRepository learnerRecordRepository,
                                LearnerRecordEventRepository learnerRecordEventepository,
                                LearnerRecordFactory learnerRecordFactory,
                                LearnerRecordEventFactory learnerRecordEventFactory) {
        this.utilService = utilService;
        this.learnerRecordRepository = learnerRecordRepository;
        this.learnerRecordEventepository = learnerRecordEventepository;
        this.learnerRecordFactory = learnerRecordFactory;
        this.learnerRecordEventFactory = learnerRecordEventFactory;
    }

    public Page<LearnerRecordDto> getRecords(Pageable pageableParams, LearnerRecordQuery learnerRecordQuery) {
        Page<LearnerRecord> results = learnerRecordRepository.find(learnerRecordQuery.getLearnerId(), learnerRecordQuery.getResourceId(),
                learnerRecordQuery.getLearnerRecordTypes(), pageableParams);
        List<LearnerRecordDto> dtos = results.get().map(this.learnerRecordFactory::createLearnerRecordDto).toList();
        return new PageImpl<>(dtos, pageableParams, dtos.size());
    }

    public LearnerRecordDto getRecord(Long id, LearnerRecordQuery learnerRecordQuery) {
        LearnerRecord record = learnerRecordRepository.findById(id).orElseThrow(() -> new LearnerRecordNotFoundException(id));
        return learnerRecordFactory.createLearnerRecordDto(record, learnerRecordQuery.isGetChildRecords(), false);
    }

    private LearnerRecord createRecordWithParent(Long parentId, CreateLearnerRecordDto dto) {
        LearnerRecord parent = learnerRecordRepository.findById(parentId)
                .orElseThrow(() -> new LearnerRecordNotFoundException(parentId));
        return learnerRecordFactory.createLearnerRecord(parent, dto);
    }

    public LearnerRecordDto createRecord(CreateLearnerRecordDto dto) {
        LearnerRecord record = dto.getParentId() != null ?
                createRecordWithParent(dto.getParentId(), dto) :
                learnerRecordFactory.createLearnerRecord(dto);
        learnerRecordRepository.save(record);
        return learnerRecordFactory.createLearnerRecordDto(record, true, true);
    }

    public List<LearnerRecordEventDto> createEvents(Long id, List<CreateLearnerRecordEventDto> dtos) {
        LearnerRecord record = learnerRecordRepository.findById(id)
                .orElseThrow(() -> new LearnerRecordNotFoundException(id));
        List<LearnerRecordEvent> events = dtos.stream().map(e -> learnerRecordEventFactory.createEvent(record, e)).toList();
        record.getEvents().addAll(events);
        learnerRecordRepository.save(record);
        return events.stream().map(learnerRecordEventFactory::createDto).toList();
    }

    public Page<LearnerRecordEventDto> getEvents(Pageable pageable, Long recordId, LearnerRecordEventQuery query) {
        Page<LearnerRecordEvent> events = learnerRecordEventepository.find(recordId, query.getEventTypes(), null,
                utilService.localDateTimeToInstant(query.getBefore()), utilService.localDateTimeToInstant(query.getAfter()), pageable);
        return learnerRecordEventFactory.createDtos(pageable, events);
    }
}
