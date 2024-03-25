package uk.gov.cslearning.record.repository;

import com.google.common.collect.Iterables;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.domain.State;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class CourseRecordRepositoryTest {

    @Autowired
    private CourseRecordRepository courseRecordRepository;

    @Test
    public void shouldSaveCourseRecordWithModuleRecord() {

        CourseRecord courseRecord = createRegistration("userId", "eventId");

        assertThat(courseRecord.getCourseId(), notNullValue());
        assertThat(courseRecord.getUserId(), notNullValue());
        assertThat(courseRecord.getModuleRecord("moduleId").getId(), notNullValue());
    }

    @Test
    public void shouldLoadCountOfRegistrationsForAnEvent() {

        final String eventId = "eventId";
        final int registrations = 3;

        for (int i = 0; i < registrations; i++) {
            createRegistration("user" + i, eventId);
        }

        Integer count = courseRecordRepository.countRegisteredForEvent(eventId);

        assertThat(count, is(registrations));
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

        Iterable<CourseRecord> records = courseRecordRepository.listEventRecords();

        assertThat(Iterables.size(records), is(registrations));
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
