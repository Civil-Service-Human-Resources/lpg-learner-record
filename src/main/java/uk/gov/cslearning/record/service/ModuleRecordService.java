package uk.gov.cslearning.record.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.dto.ModuleRecordDto;
import uk.gov.cslearning.record.dto.factory.ModuleRecordDtoFactory;
import uk.gov.cslearning.record.repository.ModuleRecordRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ModuleRecordService {

    private final ModuleRecordRepository moduleRecordRepository;
    private final ModuleRecordDtoFactory moduleRecordDtoFactory;

    public ModuleRecordService(ModuleRecordRepository moduleRecordRepository, ModuleRecordDtoFactory moduleRecordDtoFactory) {
        this.moduleRecordRepository = moduleRecordRepository;
        this.moduleRecordDtoFactory = moduleRecordDtoFactory;
    }

    @Transactional(readOnly = true)
    public List<ModuleRecordDto> listRecordsForPeriod(LocalDate periodStart, LocalDate periodEnd) {
        return moduleRecordRepository
                .findAllByCreatedAtBetweenAndCourseRecordIsNotNull(periodStart.atStartOfDay(), periodEnd.plusDays(1).atStartOfDay()).stream()
                .map(moduleRecordDtoFactory::create)
                .collect(Collectors.toList());
    }
}
