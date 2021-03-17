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
    private LocalDateTime completedAt;
    private String moduleTitle;
    private String moduleType;
    private String courseId;
    private String courseTitle;

    public ModuleRecordDto() {
    }

    public ModuleRecordDto(String moduleId, State state, String learner,
                           LocalDateTime stateChangeDate, LocalDateTime completedAt,
                           String moduleTitle, String moduleType, String courseId, String courseTitle) {
        this.moduleId = moduleId;
        this.state = String.valueOf(state);
        this.learner = learner;
        this.stateChangeDate = stateChangeDate;
        this.completedAt = completedAt;
        this.moduleTitle = moduleTitle;
        this.moduleType = moduleType;
        this.courseId = courseId;
        this.courseTitle = courseTitle;
    }
}
