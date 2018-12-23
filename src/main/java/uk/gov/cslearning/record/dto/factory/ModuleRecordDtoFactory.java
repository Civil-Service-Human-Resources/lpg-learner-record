package uk.gov.cslearning.record.dto.factory;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.dto.ModuleRecordDto;

@Component
public class ModuleRecordDtoFactory {
    public ModuleRecordDto create(ModuleRecord moduleRecord) {

        ModuleRecordDto moduleRecordDto = new ModuleRecordDto();
        moduleRecordDto.setModuleId(moduleRecord.getModuleId());
        moduleRecordDto.setState(moduleRecord.getState().toString());

        return moduleRecordDto;
    }
}
