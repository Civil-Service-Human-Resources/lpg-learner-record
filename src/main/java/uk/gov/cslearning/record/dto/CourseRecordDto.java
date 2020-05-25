package uk.gov.cslearning.record.dto;

import lombok.Data;
import uk.gov.cslearning.record.domain.State;

import java.time.LocalDateTime;

@Data
public class CourseRecordDto {
    private String courseId;
    private String learner;
    private String state;
    private String preference;
    private String profession;
    private String department;
    private LocalDateTime lastUpdated;
    private String courseTitle;

    public CourseRecordDto() {
    }

    public CourseRecordDto(String courseId, String learner, State state, String preference, LocalDateTime lastUpdated, String courseTitle) {
        this.courseId = courseId;
        this.learner = learner;
        this.state = String.valueOf(state);
        this.preference = preference;
        this.lastUpdated = lastUpdated;
        this.courseTitle = courseTitle;
    }

}
