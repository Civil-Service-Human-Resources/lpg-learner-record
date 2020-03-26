package uk.gov.cslearning.record.dto.factory;

import org.junit.Test;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.State;
import uk.gov.cslearning.record.dto.CourseRecordDto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CourseRecordDtoFactoryTest {

    private final CourseRecordDtoFactory courseRecordDtoFactory = new CourseRecordDtoFactory();

    @Test
    public void shouldReturnCourseRecordDto() {
        String courseId = "course-id";
        String userId = "user-id";
        LocalDateTime updatedAt = LocalDateTime.now().minusDays(7);
        List<CourseRecord> courseRecordList = new ArrayList<>();

        CourseRecord courseRecord = new CourseRecord("course-id", userId);
        courseRecord.setCourseTitle("CourseTitle");
        courseRecord.setPreference("Preference");
        courseRecord.setState(State.APPROVED);
        courseRecord.setLastUpdated(updatedAt);
        courseRecordList.add(courseRecord);

        List<CourseRecordDto> result = courseRecordDtoFactory.create(courseRecordList);
        assertEquals(courseId, result.get(0).getCourseId());
        assertEquals("CourseTitle", result.get(0).getCourseTitle());
        assertEquals("Preference", result.get(0).getPreference());
        assertEquals("APPROVED", result.get(0).getState());
        assertEquals(userId, result.get(0).getLearner());
        assertEquals(updatedAt, result.get(0).getLastUpdated());
    }
}