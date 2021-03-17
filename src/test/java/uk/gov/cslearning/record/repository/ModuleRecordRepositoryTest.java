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
import uk.gov.cslearning.record.dto.ModuleRecordDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class ModuleRecordRepositoryTest {

    @Autowired
    private ModuleRecordRepository moduleRecordRepository;

    @Autowired
    private CourseRecordRepository courseRecordRepository;

    @Test
    public void shouldReturnListOfRecordsCreatedBetweenTwoDates() {
        CourseRecord courseRecord = new CourseRecord("course-id", "user-id");

        LocalDateTime queryStart = LocalDateTime.now().minusDays(2);

        ModuleRecord moduleRecord1 = new ModuleRecord("moduleRecord1");
        moduleRecord1.setUpdatedAt(LocalDateTime.now().minusDays(1));
        moduleRecord1.setCourseRecord(courseRecord);

        ModuleRecord moduleRecord2 = new ModuleRecord("moduleRecord2");
        moduleRecord2.setUpdatedAt(queryStart);
        moduleRecord2.setCourseRecord(courseRecord);
        moduleRecord2.setState(State.REGISTERED);

        ModuleRecord moduleRecord3 = new ModuleRecord("moduleRecord3");
        moduleRecord3.setUpdatedAt(LocalDateTime.now().minusDays(3));
        moduleRecord3.setCourseRecord(courseRecord);

        moduleRecordRepository.saveAll(Arrays.asList(moduleRecord1, moduleRecord2, moduleRecord3));

        LocalDateTime end = LocalDateTime.now().minusDays(1).minusMinutes(1);

        List<ModuleRecordDto> results = moduleRecordRepository.findAllByCreatedAtBetweenAndCourseRecordIsNotNullNormalised(queryStart, end);

        assertEquals(1, results.size());
        assertEquals(moduleRecord2.getModuleId(), results.get(0).getModuleId());
    }

    @Test
    public void shouldReturnListOfRecordsCreatedBetweenTwoDatesForGivenLearnerIds() {

        List<String> learnerIds = new ArrayList<>();
        learnerIds.add("user-id-1");
        learnerIds.add("user-id-2");

        CourseRecord courseRecord1 = new CourseRecord("course-id-1", "user-id-1");

        LocalDateTime queryStart = LocalDateTime.now().minusDays(2);

        ModuleRecord moduleRecord1 = new ModuleRecord("moduleRecord1");
        moduleRecord1.setUpdatedAt(queryStart);
        moduleRecord1.setCourseRecord(courseRecord1);

        ModuleRecord moduleRecord2 = new ModuleRecord("moduleRecord2");
        moduleRecord2.setUpdatedAt(queryStart);
        moduleRecord2.setCourseRecord(courseRecord1);
        moduleRecord2.setState(State.REGISTERED);

        ModuleRecord moduleRecord3 = new ModuleRecord("moduleRecord3");
        moduleRecord3.setUpdatedAt(LocalDateTime.now().minusDays(3));
        moduleRecord3.setCourseRecord(courseRecord1);

        CourseRecord courseRecord2 = new CourseRecord("course-id-2", "user-id-2");

        ModuleRecord moduleRecord21 = new ModuleRecord("moduleRecord21");
        moduleRecord21.setUpdatedAt(queryStart);
        moduleRecord21.setCourseRecord(courseRecord2);

        ModuleRecord moduleRecord22 = new ModuleRecord("moduleRecord22");
        moduleRecord22.setUpdatedAt(queryStart);
        moduleRecord22.setCourseRecord(courseRecord2);
        moduleRecord22.setState(State.REGISTERED);

        ModuleRecord moduleRecord23 = new ModuleRecord("moduleRecord23");
        moduleRecord23.setUpdatedAt(LocalDateTime.now().minusDays(3));
        moduleRecord23.setCourseRecord(courseRecord2);

        moduleRecordRepository.saveAll(Arrays.asList(moduleRecord1, moduleRecord2, moduleRecord3, moduleRecord21, moduleRecord22, moduleRecord23));

        LocalDateTime end = LocalDateTime.now().minusDays(1).minusMinutes(1);

        List<ModuleRecordDto> results = moduleRecordRepository.findForLearnerIdsByCreatedAtBetweenAndCourseRecordIsNotNullNormalised(queryStart, end, learnerIds);

        assertEquals(4, results.size());
        assertEquals(moduleRecord1.getModuleId(), results.get(0).getModuleId());
    }

    @Test
    public void shouldPersistCourseRecordWithModuleRecord() {
        String courseId = "course-id";

        CourseRecord courseRecord = new CourseRecord(courseId, "user-id");
        ModuleRecord moduleRecord = new ModuleRecord("moduleRecord1");
        moduleRecord.setCourseRecord(courseRecord);

        long id = moduleRecordRepository.save(moduleRecord).getId();

        assertEquals(courseRecord, moduleRecordRepository.findById(id).get().getCourseRecord());
    }
}
