package uk.gov.cslearning.record.dto;

import lombok.Data;

@Data
public class LearnerRecordSummary {
    private String courseIdentifier;
    private String courseName;
    private String moduleIdentifier;
    private String moduleName;
    private Long timeTaken;
    private String type;
    private Integer completed = 0;
    private Integer inProgress = 0;
    private Integer notStarted = 0;


    public void incrementCompleted() {
        completed += 1;
    }

    public void incrementInProgress() {
        inProgress += 1;
    }

    public void incrementNotStarted() {
        notStarted += 1;
    }
}
