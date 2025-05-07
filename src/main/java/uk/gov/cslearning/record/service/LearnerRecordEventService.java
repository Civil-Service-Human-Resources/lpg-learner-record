package uk.gov.cslearning.record.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.api.output.BulkCreateOutput;
import uk.gov.cslearning.record.api.output.FailedResource;
import uk.gov.cslearning.record.api.record.LearnerRecordEventQuery;
import uk.gov.cslearning.record.domain.record.LearnerRecord;
import uk.gov.cslearning.record.domain.record.event.LearnerRecordEvent;
import uk.gov.cslearning.record.dto.record.CreateLearnerRecordEventDto;
import uk.gov.cslearning.record.dto.record.LearnerRecordEventDto;
import uk.gov.cslearning.record.exception.LearnerRecordNotFoundException;
import uk.gov.cslearning.record.repository.LearnerRecordEventRepository;
import uk.gov.cslearning.record.repository.LearnerRecordRepository;
import uk.gov.cslearning.record.service.factory.LearnerRecordEventFactory;
import uk.gov.cslearning.record.util.IUtilService;

import java.util.ArrayList;
import java.util.List;

@Service
public class LearnerRecordEventService {

    private final IUtilService utilService;
    private final LearnerRecordEventFactory learnerRecordEventFactory;
    private final LearnerRecordEventRepository learnerRecordEventRepository;
    private final LearnerRecordRepository learnerRecordRepository;
    private final CourseCompletionService courseCompletionService;

    public LearnerRecordEventService(IUtilService utilService, LearnerRecordEventFactory learnerRecordEventFactory,
                                     LearnerRecordEventRepository learnerRecordEventRepository, LearnerRecordRepository learnerRecordRepository,
                                     CourseCompletionService courseCompletionService) {
        this.utilService = utilService;
        this.learnerRecordEventFactory = learnerRecordEventFactory;
        this.learnerRecordEventRepository = learnerRecordEventRepository;
        this.learnerRecordRepository = learnerRecordRepository;
        this.courseCompletionService = courseCompletionService;
    }

    public Page<LearnerRecordEventDto> getRecords(Pageable pageableParams, LearnerRecordEventQuery query) {
        Page<LearnerRecordEvent> events = learnerRecordEventRepository.find(null, query.getEventTypes(),
                query.getUserId(), utilService.localDateTimeToInstant(query.getBefore()), utilService.localDateTimeToInstant(query.getAfter()), pageableParams);
        return learnerRecordEventFactory.createDtos(pageableParams, events);
    }

    public BulkCreateOutput<LearnerRecordEventDto, CreateLearnerRecordEventDto> createRecord(List<CreateLearnerRecordEventDto> dtos) {
        List<FailedResource<CreateLearnerRecordEventDto>> failures = new ArrayList<>();
        List<LearnerRecordEventDto> successful = new ArrayList<>();
        for (CreateLearnerRecordEventDto dto : dtos) {
            try {
                LearnerRecord record;
                Long learnerRecordId = dto.getLearnerRecordId();
                if (learnerRecordId != null) {
                    record = learnerRecordRepository.findById(learnerRecordId)
                            .orElseThrow(() -> new LearnerRecordNotFoundException(learnerRecordId));
                } else {
                    record = learnerRecordRepository.find(List.of(dto.getLearnerId()), List.of(dto.getResourceId()), null, null)
                            .stream().findFirst().orElseThrow(() -> new LearnerRecordNotFoundException(dto.getLearnerId(), dto.getResourceId()));
                }
                LearnerRecordEvent event = learnerRecordEventFactory.createEvent(record, dto);
                record.addEvent(event);
                learnerRecordRepository.save(record);
                courseCompletionService.checkAndCompleteCourseRecord(event);
                LearnerRecordEventDto response = learnerRecordEventFactory.createDto(event);
                successful.add(response);
            } catch (Exception e) {
                FailedResource<CreateLearnerRecordEventDto> failedResource = new FailedResource<>(dto, e.getMessage());
                failures.add(failedResource);
            }
        }
        return new BulkCreateOutput<>(successful, failures);
    }
}
