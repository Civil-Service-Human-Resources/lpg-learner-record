package uk.gov.cslearning.record.dto.factory;

import org.junit.Test;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.domain.State;
import uk.gov.cslearning.record.dto.ModuleRecordDto;

import static org.junit.Assert.assertEquals;

public class ModuleRecordDtoFactoryTest {

    private final ModuleRecordDtoFactory moduleRecordDtoFactory = new ModuleRecordDtoFactory();

    @Test
    public void shouldReturnModuleRecordDto() {
        String moduleId = "module-id";
        String userId = "user-id";

        CourseRecord courseRecord = new CourseRecord("course-id", userId);

        ModuleRecord moduleRecord = new ModuleRecord(moduleId);
        moduleRecord.setState(State.APPROVED);
        moduleRecord.setCourseRecord(courseRecord);

        ModuleRecordDto moduleRecordDto = moduleRecordDtoFactory.create(moduleRecord);
        assertEquals(moduleId, moduleRecordDto.getModuleId());
        assertEquals("APPROVED", moduleRecordDto.getState());
        assertEquals(userId, moduleRecordDto.getLearner());
    }
}