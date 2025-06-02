package uk.gov.cslearning.record.service;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.State;
import uk.gov.cslearning.record.domain.record.LearnerRecordTypeEnum;
import uk.gov.cslearning.record.domain.record.event.LearnerRecordEvent;
import uk.gov.cslearning.record.exception.CourseRecordNotFoundException;
import uk.gov.cslearning.record.repository.CourseRecordRepository;

import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Temporary class to support completing legacy course records, until we retire the table
 */
@Service
public class CourseCompletionService {

    private final CourseRecordRepository courseRecordRepository;
    private final Clock clock;

    public CourseCompletionService(CourseRecordRepository courseRecordRepository, Clock clock) {
        this.courseRecordRepository = courseRecordRepository;
        this.clock = clock;
    }

    /**
     * Performing a hardcoded check on the event type like this is not ideal, but necessary for now
     *
     * @param learnerRecordEvent
     */
    public void checkAndCompleteCourseRecord(LearnerRecordEvent learnerRecordEvent) {
        if (learnerRecordEvent.getEventType().getRecordType().getRecordType().equals(LearnerRecordTypeEnum.COURSE.name()) &&
                learnerRecordEvent.getEventType().getEventType().equals("COMPLETE_COURSE")) {
            String userId = learnerRecordEvent.getLearnerRecord().getLearnerId();
            String courseId = learnerRecordEvent.getLearnerRecord().getResourceId();
            CourseRecord courseRecord = courseRecordRepository.getCourseRecord(userId, courseId)
                    .orElseThrow(() -> new CourseRecordNotFoundException(userId, courseId));
            courseRecord.setState(State.COMPLETED);
            courseRecord.setLastUpdated(LocalDateTime.ofInstant(learnerRecordEvent.getEventTimestamp(), clock.getZone()));
            courseRecordRepository.save(courseRecord);
        }
    }

}
