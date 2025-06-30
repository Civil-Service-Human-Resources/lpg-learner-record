package uk.gov.cslearning.record.api.record;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import uk.gov.cslearning.record.api.output.BulkCreateOutput;
import uk.gov.cslearning.record.dto.record.CreateEvent;
import uk.gov.cslearning.record.dto.record.CreateLearnerRecordEventDto;
import uk.gov.cslearning.record.dto.record.LearnerRecordEventDto;
import uk.gov.cslearning.record.service.LearnerRecordEventService;

import java.util.List;

@RestController
@RequestMapping("/learner_record_events")
public class LearnerRecordEventController {

    private final LearnerRecordEventService learnerRecordEventService;

    public LearnerRecordEventController(LearnerRecordEventService learnerRecordEventService) {
        this.learnerRecordEventService = learnerRecordEventService;
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public Page<LearnerRecordEventDto> getEvents(@PageableDefault(sort = {"eventTimestamp", "id"}, direction = Sort.Direction.ASC) Pageable pageableParams,
                                                 LearnerRecordEventQuery query) {
        return learnerRecordEventService.getRecords(pageableParams, query);
    }

    @PostMapping
    public ResponseEntity<BulkCreateOutput<LearnerRecordEventDto, CreateLearnerRecordEventDto>> createEvents(@RequestBody @Validated(CreateEvent.class) List<CreateLearnerRecordEventDto> dto) {
        BulkCreateOutput<LearnerRecordEventDto, CreateLearnerRecordEventDto> response = learnerRecordEventService.createRecord(dto);
        return ResponseEntity.status(response.getSuccessfulResources().size() == 0 ? HttpStatus.BAD_REQUEST : HttpStatus.CREATED)
                .body(response);
    }

}
