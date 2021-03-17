package uk.gov.cslearning.record.api;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cslearning.record.dto.ModuleRecordDto;
import uk.gov.cslearning.record.service.ModuleRecordService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reporting")
public class ModuleRecordController {

    private final ModuleRecordService moduleRecordService;

    public ModuleRecordController(ModuleRecordService moduleRecordService) {
        this.moduleRecordService = moduleRecordService;
    }

    @GetMapping(value = "/module-records", params = {"from", "to"})
    public ResponseEntity<List<ModuleRecordDto>> listForPeriod(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return ResponseEntity.ok(moduleRecordService.listRecordsForPeriod(from, to));
    }

    @GetMapping(value = "/module-records-for-learners", params = {"from", "to", "learnerIds"})
    public ResponseEntity<List<ModuleRecordDto>> listModuleRecordsForPeriodAndLearnerIds(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam List<String> learnerIds
    ) {
        return ResponseEntity.ok(moduleRecordService.listRecordsForPeriodAndLearnerIds(from, to, learnerIds));
    }
}
