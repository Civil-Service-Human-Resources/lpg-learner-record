package uk.gov.cslearning.record.dto.factory;


import org.junit.jupiter.api.Test;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.domain.State;
import uk.gov.cslearning.record.dto.ModuleRecordDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ModuleRecordDtoFactoryTest {

    private final ModuleRecordDtoFactory moduleRecordDtoFactory = new ModuleRecordDtoFactory();

    @Test
    public void shouldReturnModuleRecordDtoWithCompletionDate() {
        String moduleId = "module-id";
        String userId = "user-id";
        LocalDateTime completionDate = LocalDateTime.now().minusDays(7);
        LocalDateTime updatedAt = LocalDateTime.now().minusDays(7);

        CourseRecord courseRecord = new CourseRecord("course-id", userId);

        ModuleRecord moduleRecord = new ModuleRecord(moduleId);
        moduleRecord.setState(State.APPROVED);
        moduleRecord.setCourseRecord(courseRecord);
        moduleRecord.setCompletionDate(completionDate);
        moduleRecord.setUpdatedAt(updatedAt);

        ModuleRecordDto moduleRecordDto = moduleRecordDtoFactory.create(moduleRecord);
        assertEquals(moduleId, moduleRecordDto.getModuleId());
        assertEquals("APPROVED", moduleRecordDto.getState());
        assertEquals(userId, moduleRecordDto.getLearner());
        assertEquals(completionDate, moduleRecordDto.getStateChangeDate());
    }

    @Test
    public void shouldReturnModuleRecordDtoWithUpdateAtDate() {
        String moduleId = "module-id";
        String userId = "user-id";
        LocalDateTime updatedAt = LocalDateTime.now().minusDays(7);

        CourseRecord courseRecord = new CourseRecord("course-id", userId);

        ModuleRecord moduleRecord = new ModuleRecord(moduleId);
        moduleRecord.setState(State.APPROVED);
        moduleRecord.setCourseRecord(courseRecord);
        moduleRecord.setUpdatedAt(updatedAt);

        ModuleRecordDto moduleRecordDto = moduleRecordDtoFactory.create(moduleRecord);
        assertEquals(moduleId, moduleRecordDto.getModuleId());
        assertEquals("APPROVED", moduleRecordDto.getState());
        assertEquals(userId, moduleRecordDto.getLearner());
        assertEquals(updatedAt, moduleRecordDto.getStateChangeDate());
    }
}
