package uk.gov.cslearning.record.dto.factory;

import org.junit.Test;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.State;
import uk.gov.cslearning.record.dto.CourseRecordDto;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

public class CourseRecordDtoFactoryTest {

    private final CourseRecordDtoFactory courseRecordDtoFactory = new CourseRecordDtoFactory();

    @Test
    public void shouldReturnCourseRecordDto() {
        String courseId = "course-id";
        String userId = "user-id";
        LocalDateTime updatedAt = LocalDateTime.now().minusDays(7);

        CourseRecord courseRecord = new CourseRecord("course-id", userId);
        courseRecord.setCourseTitle("CourseTitle");
        courseRecord.setPreference("Preference");
        courseRecord.setState(State.APPROVED);
        courseRecord.setLastUpdated(updatedAt);

        CourseRecordDto result = courseRecordDtoFactory.create(courseRecord);
        assertEquals(courseId, result.getCourseId());
        assertEquals("CourseTitle", result.getCourseTitle());
        assertEquals("Preference", result.getPreference());
        assertEquals("APPROVED", result.getState());
        assertEquals(userId, result.getLearner());
        assertEquals(updatedAt, result.getLastUpdated());
    }
}