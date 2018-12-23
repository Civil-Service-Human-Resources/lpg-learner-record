package uk.gov.cslearning.record.dto.factory;

import org.junit.Test;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.domain.State;
import uk.gov.cslearning.record.dto.ModuleRecordDto;

import static org.junit.Assert.assertEquals;

public class ModuleRecordDtoFactoryTest {

    private final ModuleRecordDtoFactory moduleRecordDtoFactory = new ModuleRecordDtoFactory();

    @Test
    public void shouldReturnModuleRecordDto() {
        String moduleId = "module-id";

        ModuleRecord moduleRecord = new ModuleRecord(moduleId);
        moduleRecord.setState(State.APPROVED);

        ModuleRecordDto moduleRecordDto = moduleRecordDtoFactory.create(moduleRecord);
        assertEquals(moduleId, moduleRecordDto.getModuleId());
        assertEquals("APPROVED", moduleRecordDto.getState());
    }
}