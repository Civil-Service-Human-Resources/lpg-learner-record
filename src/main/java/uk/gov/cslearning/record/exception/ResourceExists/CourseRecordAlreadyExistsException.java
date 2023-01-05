package uk.gov.cslearning.record.exception.ResourceExists;

public class CourseRecordAlreadyExistsException extends ResourceExistsException {

    public CourseRecordAlreadyExistsException(String courseId, String userId) {
        super(String.format("Course record already exists for course %s,and user %s", courseId, userId));
    }
}
