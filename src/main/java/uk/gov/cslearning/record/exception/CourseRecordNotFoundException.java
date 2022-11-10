package uk.gov.cslearning.record.exception;

public class CourseRecordNotFoundException extends RuntimeException {
    public CourseRecordNotFoundException(String userId, String courseId) {
        super(String.format("Course Record does not exist with learner ID: %s and Course ID: %s", userId, courseId));
    }
}
