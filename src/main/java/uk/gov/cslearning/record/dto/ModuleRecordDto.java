package uk.gov.cslearning.record.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ModuleRecordDto {
    private String moduleId;
    private String state;
    private String learner;
    private LocalDateTime stateChangeDate;
}
