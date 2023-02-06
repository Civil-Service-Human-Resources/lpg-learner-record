package uk.gov.cslearning.record.api;

import com.github.fge.jsonpatch.JsonPatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.cslearning.record.api.input.POST.PostModuleRecordInput;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.service.ModuleRecordService;

import javax.validation.Valid;

@RestController
@RequestMapping("/module_records")
@Slf4j
public class ModuleRecordController {

    private final ModuleRecordService moduleRecordService;

    public ModuleRecordController(ModuleRecordService moduleRecordService) {
        this.moduleRecordService = moduleRecordService;
    }

    @PatchMapping(path = "/{moduleRecordId}", consumes = "application/json-patch+json")
    public ResponseEntity<ModuleRecord> updateModuleRecord(@PathVariable("moduleRecordId") Long moduleRecordId,
                                                           @RequestBody JsonPatch patchData) {
        log.debug("LC-1627: ModuleRecordController.updateModuleRecord.received moduleRecordId: {} ", moduleRecordId);
        ModuleRecord updatedRecord = moduleRecordService.updateModuleRecord(moduleRecordId, patchData);
        log.debug("LC-1627: ModuleRecordController.updateModuleRecord.returning updatedRecord: {} ", updatedRecord);
        return new ResponseEntity<>(updatedRecord, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ModuleRecord> createModuleRecord(@Valid @RequestBody PostModuleRecordInput newModule) {
        log.debug("LC-1627: ModuleRecordController.createModuleRecord.received newModule: {} ", newModule);
        ModuleRecord createdModule = moduleRecordService.createModuleRecord(newModule);
        log.debug("LC-1627: ModuleRecordController.createModuleRecord.returning createdModule: {} ", createdModule);
        return new ResponseEntity<>(createdModule, HttpStatus.CREATED);
    }
}
