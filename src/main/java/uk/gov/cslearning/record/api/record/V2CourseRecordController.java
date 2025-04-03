package uk.gov.cslearning.record.api.record;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import uk.gov.cslearning.record.domain.record.LearnerRecordTypeEnum;
import uk.gov.cslearning.record.dto.record.CourseRecordController;
import uk.gov.cslearning.record.dto.record.CreateLearnerRecordDto;
import uk.gov.cslearning.record.dto.record.LearnerRecordDto;
import uk.gov.cslearning.record.service.LearnerRecordService;

import java.util.List;

@RestController
@RequestMapping("/v2/course_records")
public class V2CourseRecordController {

    private final LearnerRecordService learnerRecordService;

    public V2CourseRecordController(LearnerRecordService learnerRecordService) {
        this.learnerRecordService = learnerRecordService;
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public Page<LearnerRecordDto> getRecords(@PageableDefault(sort = {"createdTimestamp"}, direction = Sort.Direction.ASC) Pageable pageableParams,
                                             @Validated(CourseRecordController.class) LearnerRecordQuery learnerRecordQuery) {
        learnerRecordQuery.setLearnerRecordTypes(List.of(LearnerRecordTypeEnum.COURSE.getId()));
        return learnerRecordService.getRecords(pageableParams, learnerRecordQuery);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public LearnerRecordDto createRecord(@RequestBody @Validated(CourseRecordController.class) CreateLearnerRecordDto dto) {
        dto.setRecordType(LearnerRecordTypeEnum.COURSE.getId());
        return learnerRecordService.createRecord(dto);
    }

}
