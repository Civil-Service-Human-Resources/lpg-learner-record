package uk.gov.cslearning.record.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class CourseRecordIdentity implements Serializable {

    @Column(nullable = false)
    private String courseId;

    @Column(nullable = false)
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CourseRecordIdentity that = (CourseRecordIdentity) o;

        if (!courseId.equals(that.courseId)) return false;
        return userId.equals(that.userId);
    }

    @Override
    public int hashCode() {
        int result = courseId.hashCode();
        result = 31 * result + userId.hashCode();
        return result;
    }
}
