package uk.gov.cslearning.record.dto;

import lombok.Data;

@Data
public class CourseRecordDto {
    private String courseTitle;

    public CourseRecordDto(String courseTitle) {
        this.courseTitle = courseTitle;
    }
}

