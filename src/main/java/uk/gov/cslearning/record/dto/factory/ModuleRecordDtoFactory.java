package uk.gov.cslearning.record.dto.factory;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.dto.ModuleRecordDto;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class ModuleRecordDtoFactory {
    public ModuleRecordDto create(ModuleRecord moduleRecord) {
        ModuleRecordDto moduleRecordDto = new ModuleRecordDto();
        moduleRecordDto.setModuleId(moduleRecord.getModuleId());
        if (moduleRecord.getState() != null) {
            moduleRecordDto.setState(moduleRecord.getState().toString());
        }
        moduleRecordDto.setLearner(moduleRecord.getCourseRecord().getUserId());

        LocalDateTime stateChangeDate =
                Optional.ofNullable(moduleRecord.getCompletionDate())
                        .orElse(moduleRecord.getUpdatedAt());

        moduleRecordDto.setStateChangeDate(stateChangeDate);

        return moduleRecordDto;
    }
}
