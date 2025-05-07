package uk.gov.cslearning.record.api.record;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uk.gov.cslearning.record.api.output.BulkCreateOutput;
import uk.gov.cslearning.record.dto.record.CreateLearnerRecordDto;
import uk.gov.cslearning.record.dto.record.CreateLearnerRecordEventDto;
import uk.gov.cslearning.record.dto.record.LearnerRecordDto;
import uk.gov.cslearning.record.dto.record.LearnerRecordEventDto;
import uk.gov.cslearning.record.service.LearnerRecordService;

import java.util.List;

@RestController
@RequestMapping("/learner_records")
public class LeanerRecordController {

    private final LearnerRecordService learnerRecordService;

    public LeanerRecordController(LearnerRecordService learnerRecordService) {
        this.learnerRecordService = learnerRecordService;
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public Page<LearnerRecordDto> getRecords(@PageableDefault(sort = {"createdTimestamp"}, direction = Sort.Direction.ASC) Pageable pageableParams,
                                             LearnerRecordQuery learnerRecordQuery) {
        return learnerRecordService.getRecords(pageableParams, learnerRecordQuery);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public LearnerRecordDto getRecord(@PathVariable Long id, LearnerRecordQuery learnerRecordQuery) {
        return learnerRecordService.getRecord(id, learnerRecordQuery);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public LearnerRecordDto createRecord(@RequestBody @Valid CreateLearnerRecordDto dto) {
        return learnerRecordService.createRecord(dto);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/bulk")
    public BulkCreateOutput<LearnerRecordDto, CreateLearnerRecordDto> createRecords(@RequestBody @Valid List<CreateLearnerRecordDto> dtos) {
        return learnerRecordService.createRecords(dtos);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{id}/events")
    public List<LearnerRecordEventDto> createEvents(@PathVariable Long id, @RequestBody @Valid List<CreateLearnerRecordEventDto> dtos) {
        return learnerRecordService.createEvents(id, dtos);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}/events")
    public Page<LearnerRecordEventDto> getEvents(@PathVariable Long id,
                                                 @PageableDefault(sort = {"eventTimestamp"}, direction = Sort.Direction.ASC) Pageable pageableParams,
                                                 LearnerRecordEventQuery query) {
        return learnerRecordService.getEvents(pageableParams, id, query);
    }
}
