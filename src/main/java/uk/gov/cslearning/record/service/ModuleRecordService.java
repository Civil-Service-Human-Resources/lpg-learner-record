package uk.gov.cslearning.record.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.dto.ModuleRecordDto;
import uk.gov.cslearning.record.repository.ModuleRecordRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class ModuleRecordService {

    private final ModuleRecordRepository moduleRecordRepository;

    public ModuleRecordService(ModuleRecordRepository moduleRecordRepository) {
        this.moduleRecordRepository = moduleRecordRepository;
    }

    @Transactional(readOnly = true)
    public List<ModuleRecordDto> listRecordsForPeriod(LocalDate periodStart, LocalDate periodEnd) {
        return moduleRecordRepository
                .findAllByCreatedAtBetweenAndCourseRecordIsNotNullNormalised(periodStart.atStartOfDay(), periodEnd.plusDays(1).atStartOfDay());
    }
}
