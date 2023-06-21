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
        courseRecord1.setCourseTitle("courseRecord1Title");

        LocalDateTime queryStart = LocalDateTime.now().minusDays(2);

        ModuleRecord moduleRecord11 = new ModuleRecord("module-id-11");
        moduleRecord11.setModuleTitle("moduleRecord11Title");
        moduleRecord11.setModuleType("moduleRecord11Type");
        moduleRecord11.setUpdatedAt(queryStart);
        moduleRecord11.setCompletionDate(queryStart);
        moduleRecord11.setState(State.COMPLETED);
        moduleRecord11.setCourseRecord(courseRecord1);

        ModuleRecord moduleRecord12 = new ModuleRecord("module-id-12");
        moduleRecord12.setModuleTitle("moduleRecord12Title");
        moduleRecord12.setModuleType("moduleRecord12Type");
        moduleRecord12.setUpdatedAt(queryStart);
        moduleRecord12.setCompletionDate(queryStart);
        moduleRecord12.setState(State.REGISTERED);
        moduleRecord12.setCourseRecord(courseRecord1);

        ModuleRecord moduleRecord13 = new ModuleRecord("module-id-13");
        moduleRecord13.setModuleTitle("moduleRecord13Title");
        moduleRecord13.setModuleType("moduleRecord13Type");
        moduleRecord13.setUpdatedAt(queryStart);
        moduleRecord13.setCompletionDate(queryStart);
        moduleRecord13.setState(State.IN_PROGRESS);
        moduleRecord13.setCourseRecord(courseRecord1);

        CourseRecord courseRecord2 = new CourseRecord("course-id-2", "user-id-2");
        courseRecord2.setCourseTitle("courseRecord2Title");

        ModuleRecord moduleRecord21 = new ModuleRecord("module-id-21");
        moduleRecord21.setModuleTitle("moduleRecord21Title");
        moduleRecord21.setModuleType("moduleRecord21Type");
        moduleRecord21.setUpdatedAt(queryStart);
        moduleRecord21.setCompletionDate(queryStart);
        moduleRecord21.setState(State.APPROVED);
        moduleRecord21.setCourseRecord(courseRecord2);

        ModuleRecord moduleRecord22 = new ModuleRecord("module-id-22");
        moduleRecord22.setModuleTitle("moduleRecord22Title");
        moduleRecord22.setModuleType("moduleRecord21Type");
        moduleRecord22.setUpdatedAt(queryStart);
        moduleRecord22.setCompletionDate(queryStart);
        moduleRecord22.setState(State.REGISTERED);
        moduleRecord22.setCourseRecord(courseRecord2);

        ModuleRecord moduleRecord23 = new ModuleRecord("module-id-23");
        moduleRecord23.setModuleTitle("moduleRecord23Title");
        moduleRecord23.setModuleType("moduleRecord23Type");
        moduleRecord23.setUpdatedAt(LocalDateTime.now().minusDays(3));
        moduleRecord23.setCompletionDate(queryStart);
        moduleRecord23.setState(State.REGISTERED);
        moduleRecord23.setCourseRecord(courseRecord2);

        List<ModuleRecord> moduleRecords = Arrays.asList(moduleRecord11, moduleRecord12, moduleRecord13, moduleRecord21, moduleRecord22, moduleRecord23);
        moduleRecordRepository.saveAll(moduleRecords);

        LocalDateTime end = LocalDateTime.now().minusDays(1).minusMinutes(1);
        List<ModuleRecordDto> results = moduleRecordRepository.findForLearnerIdsByCreatedAtBetweenAndCourseRecordIsNotNullNormalised(queryStart, end, learnerIds);

        assertEquals(5, results.size());
        for(int i = 0; i < results.size(); i++) {
            assertEquals(moduleRecords.get(i).getModuleId(), results.get(i).getModuleId());
            assertEquals(moduleRecords.get(i).getModuleTitle(), results.get(i).getModuleTitle());
            assertEquals(moduleRecords.get(i).getModuleType(), results.get(i).getModuleType());
            assertEquals(moduleRecords.get(i).getUpdatedAt(), results.get(i).getStateChangeDate());
            assertEquals(moduleRecords.get(i).getCompletionDate(), results.get(i).getCompletedAt());
            assertEquals(moduleRecords.get(i).getState().name(), results.get(i).getState());
            assertEquals(moduleRecords.get(i).getCourseRecord().getCourseId(), results.get(i).getCourseId());
            assertEquals(moduleRecords.get(i).getCourseRecord().getCourseTitle(), results.get(i).getCourseTitle());
            assertEquals(moduleRecords.get(i).getCourseRecord().getUserId(), results.get(i).getLearner());
            assertEquals(moduleRecords.get(i).getCourseRecord().getIdentity().getUserId(), results.get(i).getLearner());
        }
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

    @Test
    public void shouldBeAbleToSetAndGetModuleRecordUid(){
        ModuleRecord moduleRecord = new ModuleRecord("moduleRecord1");
        moduleRecord.setUid("uid123");
        assertEquals(moduleRecord.getUid(), "uid123");
    }
}
