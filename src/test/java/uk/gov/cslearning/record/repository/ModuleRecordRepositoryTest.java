package uk.gov.cslearning.record.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.domain.State;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class ModuleRecordRepositoryTest {

    @Autowired
    private ModuleRecordRepository moduleRecordRepository;


    @Test
    public void shouldPersistCourseRecordWithModuleRecord() {
        String courseId = "course-id";

        CourseRecord courseRecord = new CourseRecord(courseId, "user-id");
        courseRecord.setCourseTitle("title");
        ModuleRecord moduleRecord = new ModuleRecord("moduleRecord1");
        moduleRecord.setState(State.IN_PROGRESS);
        moduleRecord.setModuleType("elearning");
        moduleRecord.setModuleTitle("title");
        moduleRecord.setCourseRecord(courseRecord);

        long id = moduleRecordRepository.save(moduleRecord).getId();

        assertEquals(courseRecord, moduleRecordRepository.findById(id).get().getCourseRecord());
    }

    @Test
    public void shouldBeAbleToSetAndGetModuleRecordUid() {
        ModuleRecord moduleRecord = new ModuleRecord("moduleRecord1");
        moduleRecord.setUid("uid123");
        assertEquals(moduleRecord.getUid(), "uid123");
    }
}
