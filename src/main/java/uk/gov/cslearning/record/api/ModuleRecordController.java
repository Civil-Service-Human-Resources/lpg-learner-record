package uk.gov.cslearning.record.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import uk.gov.cslearning.record.api.input.CreateModuleRecord;
import uk.gov.cslearning.record.api.input.UpdateModuleRecord;
import uk.gov.cslearning.record.api.output.ModuleRecordOutput;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.service.ModuleRecordService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/module_records")
public class ModuleRecordController {

    private final ModuleRecordService moduleRecordService;

    public ModuleRecordController(ModuleRecordService moduleRecordService) {
        this.moduleRecordService = moduleRecordService;
    }

    @PostMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ModuleRecord createModuleRecord(@Validated(CreateModuleRecord.class) @RequestBody ModuleRecord inputModule) {
        return moduleRecordService.createModuleRecord(inputModule);
    }

    @PostMapping("/bulk")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ModuleRecordOutput createModuleRecords(@Validated(CreateModuleRecord.class) @RequestBody @Size(min = 1) List<ModuleRecord> inputModules) {
        return new ModuleRecordOutput(inputModules.stream().map(moduleRecordService::createModuleRecord).toList());
    }

    @GetMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ModuleRecordOutput fetchModuleRecords(@Valid FetchModuleRecordParams fetchModuleRecordParams) {
        List<ModuleRecord> moduleRecords = moduleRecordService.fetchModuleRecords(fetchModuleRecordParams);
        return new ModuleRecordOutput(moduleRecords);
    }

    @PutMapping("/{moduleRecordId}")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ModuleRecord updateModuleRecord(@PathVariable Long moduleRecordId, @Valid @RequestBody ModuleRecord moduleRecord) {
        return moduleRecordService.updateModuleRecord(moduleRecordId, moduleRecord);
    }

    @PutMapping("/bulk")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ModuleRecordOutput updateModuleRecords(@Validated(UpdateModuleRecord.class) @RequestBody @Size(min = 1) List<ModuleRecord> moduleRecords) {
        return new ModuleRecordOutput(moduleRecords.stream().map(mr -> moduleRecordService.updateModuleRecord(mr.getId(), mr)).toList());
    }
}
