package uk.gov.cslearning.record.service;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.domain.CourseRecord;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;

@Service
public class CourseRecordService {
    
    public CourseRecordService() {
    }

    public LocalDate getMostRecentlyCompletedForCourse(Collection<CourseRecord> courseRecords) {
        LocalDate mostRecentlyCompleted = null;

        for (CourseRecord courseRecord : courseRecords) {
            LocalDateTime courseCompletionDate = courseRecord.getCompletionDate();
            if (mostRecentlyCompleted == null || courseCompletionDate != null && mostRecentlyCompleted.isBefore(courseCompletionDate.toLocalDate())) {
                mostRecentlyCompleted = courseCompletionDate.toLocalDate();
            }
        }

        return mostRecentlyCompleted;
    }
}
