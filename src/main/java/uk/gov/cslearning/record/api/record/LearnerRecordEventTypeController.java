package uk.gov.cslearning.record.api.record;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uk.gov.cslearning.record.dto.record.LearnerRecordEventTypeDto;
import uk.gov.cslearning.record.service.LookupValueService;

import java.util.List;

@RestController
@RequestMapping("/learner_record_event_types")
public class LearnerRecordEventTypeController {
    private final LookupValueService lookupValueService;

    public LearnerRecordEventTypeController(LookupValueService lookupValueService) {
        this.lookupValueService = lookupValueService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<LearnerRecordEventTypeDto> get() {
        return this.lookupValueService.getLearnerRecordEventTypes();
    }
}
