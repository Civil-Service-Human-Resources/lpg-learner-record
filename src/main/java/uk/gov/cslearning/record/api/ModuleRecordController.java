package uk.gov.cslearning.record.api;

import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.cslearning.record.api.input.POST.PostModuleRecordInput;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.service.ModuleRecordService;

import javax.validation.Valid;

@RestController
@RequestMapping("/module_records")
public class ModuleRecordController {

    private final ModuleRecordService moduleRecordService;

    public ModuleRecordController(ModuleRecordService moduleRecordService) {
        this.moduleRecordService = moduleRecordService;
    }

    @PatchMapping(path = "/{moduleRecordId}", consumes = "application/json-patch+json")
    public ResponseEntity<ModuleRecord> updateModuleRecord(@PathVariable("moduleRecordId") Long moduleRecordId,
                                                           @RequestBody JsonPatch patchData) {
        ModuleRecord updatedRecord = moduleRecordService.updateModuleRecord(moduleRecordId, patchData);
        return new ResponseEntity<>(updatedRecord, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ModuleRecord> createModuleRecord(@Valid @RequestBody PostModuleRecordInput newModule) {
        ModuleRecord createdModule = moduleRecordService.createModuleRecord(newModule);
        return new ResponseEntity<>(createdModule, HttpStatus.CREATED);
    }
}
