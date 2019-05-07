package uk.gov.cslearning.record.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.record.dto.ModuleRecordDto;
import uk.gov.cslearning.record.dto.factory.ModuleRecordDtoFactory;
import uk.gov.cslearning.record.repository.ModuleRecordRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ModuleRecordServiceTest {

    @Mock
    private ModuleRecordDtoFactory moduleRecordDtoFactory;

    @Mock
    private ModuleRecordRepository moduleRecordRepository;

    @InjectMocks
    private ModuleRecordService moduleRecordService;

    @Test
    public void shouldReturnListOfModuleRecordDtos() {
        LocalDate from = LocalDate.now().minusDays(7);
        LocalDate to = LocalDate.now();

        ModuleRecordDto moduleRecord1 = new ModuleRecordDto();
        ModuleRecordDto moduleRecord2 = new ModuleRecordDto();

        ModuleRecordDto moduleRecordDto1 = new ModuleRecordDto();
        ModuleRecordDto moduleRecordDto2 = new ModuleRecordDto();

        when(moduleRecordRepository.findAllByCreatedAtBetweenAndCourseRecordIsNotNullNormalised(from.atStartOfDay(), to.plusDays(1).atStartOfDay()))
                .thenReturn(Arrays.asList(moduleRecord1, moduleRecord2));

        assertEquals(Arrays.asList(moduleRecordDto1, moduleRecordDto2),
                moduleRecordService.listRecordsForPeriod(from, to));
    }

    @Test
    public void shouldReturnEmptyListIfNoResults() {
        LocalDate from = LocalDate.now().minusDays(7);
        LocalDate to = LocalDate.now();

        when(moduleRecordRepository.findAllByCreatedAtBetweenAndCourseRecordIsNotNullNormalised(from.atStartOfDay(), to.plusDays(1).atStartOfDay()))
                .thenReturn(new ArrayList<>());

        assertEquals(new ArrayList<>(), moduleRecordService.listRecordsForPeriod(from, to));
    }

}