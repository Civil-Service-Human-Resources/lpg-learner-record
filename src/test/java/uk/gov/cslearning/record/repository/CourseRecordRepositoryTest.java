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
import uk.gov.cslearning.record.dto.CourseRecordDto;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class CourseRecordRepositoryTest {

    @Autowired
    private CourseRecordRepository courseRecordRepository;

    @Test
    public void shouldSaveCourseRecord() {

        CourseRecord courseRecord = new CourseRecord("courseId", "userId");
        courseRecordRepository.save(courseRecord);

        assertThat(courseRecord.getIdentity(), notNullValue());
    }

    @Test
    public void shouldSaveCourseRecordWithModuleRecord() {

        CourseRecord courseRecord = new CourseRecord("courseId", "userId");
        courseRecord.addModuleRecord(new ModuleRecord("moduleId"));

        courseRecord = courseRecordRepository.save(courseRecord);

        assertThat(courseRecord.getIdentity(), notNullValue());
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
        courseRecordRepository.save(courseRecord);

        final String eventId = "eventId";
        final int registrations = 3;

        for (int i = 0; i < registrations; i++) {
            createRegistration("user" + i, eventId);
        }

        Iterable<CourseRecord> records = courseRecordRepository.listEventRecords();

        assertThat(Iterables.size(records), is(registrations));
    }

    @Test
    public void shouldReturnAllCourseRecordsByLastUpdatedBetween() {
        LocalDateTime queryStart = LocalDateTime.now().minusDays(2);

        CourseRecord courseRecord1 = new CourseRecord("course-id1", "user-id1");
        courseRecord1.setLastUpdated(LocalDateTime.now().minusDays(1));

        CourseRecord courseRecord2 = new CourseRecord("course-id2", "user-id2");
        courseRecord2.setLastUpdated(queryStart);

        CourseRecord courseRecord3 = new CourseRecord("course-id3", "user-id3");
        courseRecord3.setLastUpdated(LocalDateTime.now().minusDays(3));

        courseRecordRepository.saveAll(Arrays.asList(courseRecord1, courseRecord2, courseRecord3));

        LocalDateTime end = LocalDateTime.now().minusDays(1).minusMinutes(1);

        List<CourseRecord> results = courseRecordRepository.findAllByLastUpdatedBetween(queryStart, end);

        assertEquals(1, results.size());
    }

    private void createRegistration(String userId, String eventId) {

        ModuleRecord moduleRecord = new ModuleRecord("moduleId");
        moduleRecord.setEventId(eventId);
        moduleRecord.setState(State.REGISTERED);

        CourseRecord courseRecord = new CourseRecord("courseId", userId);
        courseRecord.addModuleRecord(moduleRecord);

        courseRecordRepository.save(courseRecord);
    }
}