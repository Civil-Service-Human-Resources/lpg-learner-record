package uk.gov.cslearning.record.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.cslearning.record.dto.factory.ModuleRecordDtoFactory;
import uk.gov.cslearning.record.repository.ModuleRecordRepository;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class ModuleRecordServicePatchTest {

    @Mock
    private ModuleRecordDtoFactory moduleRecordDtoFactory;

    @Mock
    private ModuleRecordRepository moduleRecordRepository;

    @InjectMocks
    private ModuleRecordService moduleRecordService;

    @Test
    public void shouldReturnEmptyListIfNoResults() {
        LocalDate from = LocalDate.now().minusDays(7);
        LocalDate to = LocalDate.now();

        when(moduleRecordRepository.findAllByCreatedAtBetweenAndCourseRecordIsNotNullNormalised(from.atStartOfDay(), to.plusDays(1).atStartOfDay()))
                .thenReturn(new ArrayList<>());

        assertEquals(new ArrayList<>(), moduleRecordService.listRecordsForPeriod(from, to));
    }

}
