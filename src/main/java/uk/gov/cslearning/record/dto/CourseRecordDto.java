package uk.gov.cslearning.record.dto;

import lombok.Data;
import uk.gov.cslearning.record.domain.State;

import java.time.LocalDateTime;

@Data
public class CourseRecordDto {
    private String courseId;
    private String state;
    private String preference;
    private String profession;
    private String department;
    private LocalDateTime stateChangeDate;
    private LocalDateTime completedAt;

    public CourseRecordDto() {
    }

    public CourseRecordDto(String courseId, State state, String learner, LocalDateTime stateChangeDate, LocalDateTime completedAt) {
        this.courseId = courseId;
        this.state = String.valueOf(state);
        this.learner = learner;
        this.stateChangeDate = stateChangeDate;
        this.completedAt = completedAt;
    }
}
