package uk.gov.cslearning.record.exception;

public class LearnerRecordNotFoundException extends RuntimeException {
    public LearnerRecordNotFoundException(Long learnerRecordId) {
        super(String.format("Learner record does not exist with  id: %s", learnerRecordId));
    }

    public LearnerRecordNotFoundException(String resourceId, String userId) {
        super(String.format("Learner record does not exist with  resourceID: %s and UserID: %s", resourceId, userId));
    }
}
