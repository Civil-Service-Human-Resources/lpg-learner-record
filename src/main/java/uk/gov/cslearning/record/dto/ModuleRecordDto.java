package uk.gov.cslearning.record.dto;

import lombok.Data;
import uk.gov.cslearning.record.domain.State;

import java.time.LocalDateTime;

@Data
public class ModuleRecordDto {
    private String moduleId;
    private String state;
    private String learner;
    private LocalDateTime stateChangeDate;

    public ModuleRecordDto() {
    }

    public ModuleRecordDto(String moduleId, State state, String learner, LocalDateTime stateChangeDate) {
        this.moduleId = moduleId;
        this.state = String.valueOf(state);
        this.learner = learner;
        this.stateChangeDate = stateChangeDate;
    }
}
