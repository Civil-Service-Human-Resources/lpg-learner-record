package uk.gov.cslearning.record.api;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cslearning.record.dto.ModuleRecordDto;
import uk.gov.cslearning.record.service.ModuleRecordService;

import java.util.List;

@RestController
@RequestMapping("/reporting")
public class ModuleRecordReportController {

    private final ModuleRecordService moduleRecordService;

    public ModuleRecordReportController(ModuleRecordService moduleRecordService) {
        this.moduleRecordService = moduleRecordService;
    }

    @PostMapping(value = "/module-records")
    public ResponseEntity<List<ModuleRecordDto>> listForPeriod(@RequestBody FromToParams params) {
        return ResponseEntity.ok(moduleRecordService.listRecordsForPeriod(params));
    }

    @PostMapping(value = "/module-records-for-learners")
    public ResponseEntity<List<ModuleRecordDto>> listModuleRecordsForPeriodAndLearnerIds(@Valid @RequestBody FromToParamsUserIds params) {
        return ResponseEntity.ok(moduleRecordService.listRecordsForPeriodAndLearnerIds(params));
    }

    @PostMapping(value = "/module-records-for-course-ids")
    public ResponseEntity<List<ModuleRecordDto>> listModuleRecordsForPeriodAndCourseIds(@Valid @RequestBody FromToParamsCourseIds params) {
        return ResponseEntity.ok(moduleRecordService.listRecordsForPeriodAndCourseIds(params));
    }
}
