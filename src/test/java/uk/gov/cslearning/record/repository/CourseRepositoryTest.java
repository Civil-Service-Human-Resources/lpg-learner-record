package uk.gov.cslearning.record.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class CourseRepositoryTest {

    @Autowired
    private CourseRecordRepository courseRecordRepository;

    @Test
    public void shouldSaveCourseRecord() {

        CourseRecord courseRecord = new CourseRecord("courseId", "userId");
        courseRecordRepository.save(courseRecord);

        assertThat(courseRecord.getId(), notNullValue());
    }

    @Test
    public void shouldSaveCourseRecordWithModuleRecord() {

        CourseRecord courseRecord = new CourseRecord("courseId", "userId");
        courseRecord.addModuleRecord(new ModuleRecord("moduleId"));

        courseRecordRepository.save(courseRecord);

        assertThat(courseRecord.getId(), notNullValue());
        assertThat(courseRecord.getModuleRecord("moduleId").getId(), notNullValue());
    }
}