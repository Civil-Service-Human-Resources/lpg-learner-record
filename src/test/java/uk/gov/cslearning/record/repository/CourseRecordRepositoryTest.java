package uk.gov.cslearning.record.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.IntegrationTestBase;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.domain.State;

import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
public class CourseRecordRepositoryTest extends IntegrationTestBase {

    @Autowired
    private CourseRecordRepository courseRecordRepository;

    @Test
    public void shouldSaveCourseRecordWithModuleRecord() {

        CourseRecord courseRecord = createRegistration("userId", "eventId");

        assertThat(courseRecord.getCourseId(), notNullValue());
        assertThat(courseRecord.getUserId(), notNullValue());
        assertThat(courseRecord.getModuleRecords().stream().filter(mr -> mr.getModuleId().equals("moduleId")).findFirst().get().getId(), notNullValue());
    }

    @Test
    public void shouldReturnAllCourseRecordsContainingAnEvent() {

        // Add a non-event record for good measure
        CourseRecord courseRecord = new CourseRecord("courseId", "userId");
        courseRecord.setCourseTitle("title");
        courseRecordRepository.save(courseRecord);

        final String eventId = "eventId";
        final int registrations = 3;

        for (int i = 0; i < registrations; i++) {
            createRegistration("user" + i, eventId);
        }

        List<CourseRecord> records = courseRecordRepository.listEventRecords();

        assertEquals(registrations, records.size());
    }

    private CourseRecord createRegistration(String userId, String eventId) {

        ModuleRecord moduleRecord = new ModuleRecord("moduleId");
        moduleRecord.setModuleTitle("title");
        moduleRecord.setEventId(eventId);
        moduleRecord.setModuleType("face-to-face");
        moduleRecord.setState(State.REGISTERED);

        CourseRecord courseRecord = new CourseRecord("courseId", userId);
        courseRecord.setCourseTitle("title");
        courseRecord.addModuleRecord(moduleRecord);

        return courseRecordRepository.save(courseRecord);
    }
}
