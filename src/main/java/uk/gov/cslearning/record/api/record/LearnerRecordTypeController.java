package uk.gov.cslearning.record.api.record;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uk.gov.cslearning.record.dto.record.LearnerRecordTypeDto;
import uk.gov.cslearning.record.service.LearnerRecordTypeService;

import java.util.List;

@RestController
@RequestMapping("/learner_record_types")
public class LearnerRecordTypeController {

    private final LearnerRecordTypeService learnerRecordTypeService;

    public LearnerRecordTypeController(LearnerRecordTypeService learnerRecordTypeService) {
        this.learnerRecordTypeService = learnerRecordTypeService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<LearnerRecordTypeDto> get(GetLearnerRecordTypesParams params) {
        return learnerRecordTypeService.getLearnerRecordTypes(params);
    }

}
