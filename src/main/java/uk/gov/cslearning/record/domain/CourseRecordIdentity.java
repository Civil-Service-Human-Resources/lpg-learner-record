package uk.gov.cslearning.record.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

@Embeddable
public class CourseRecordIdentity implements Serializable {

    @Column(nullable = false)
    @NotBlank(message = "courseId is required")
    private String courseId;

    @Column(nullable = false)
    @NotBlank(message = "userId is required")
    private String userId;

    public CourseRecordIdentity() {

    }

    public CourseRecordIdentity(String courseId, String userId) {
        this.courseId = courseId;
        this.userId = userId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
