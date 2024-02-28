package uk.gov.cslearning.record.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.domain.State;
import uk.gov.cslearning.record.dto.ModuleRecordDto;
import uk.gov.cslearning.record.exception.ModuleRecordNotFoundException;
import uk.gov.cslearning.record.repository.ModuleRecordRepository;
import uk.gov.cslearning.record.util.IUtilService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModuleRecordService {

    private final ModuleRecordRepository moduleRecordRepository;
    private final IUtilService utilService;

    public ModuleRecord createModuleRecord(ModuleRecord input, CourseRecord parentRecord, LocalDateTime createdTimestamp) {
        input.setCreatedAt(createdTimestamp);
        input.setUid(utilService.generateUUID());
        input.setUpdatedAt(createdTimestamp);
        if (input.getState().equals(State.COMPLETED)) {
            input.setCompletionDate(createdTimestamp);
        }
        input.setCourseRecord(parentRecord);
        return moduleRecordRepository.saveAndFlush(input);
//        return input;
    }

    public ModuleRecord updateModuleRecord(Long moduleRecordId, ModuleRecord newRecord, LocalDateTime updated) {
        ModuleRecord moduleRecord = moduleRecordRepository.findById(moduleRecordId).orElseThrow(() -> new ModuleRecordNotFoundException(moduleRecordId));
        newRecord.setUpdatedAt(updated);
        if (newRecord.getState().equals(State.COMPLETED)) {
            newRecord.setCompletionDate(updated);
        }
        if (StringUtils.isBlank(newRecord.getUid())) {
            newRecord.setUid(utilService.generateUUID());
        }
        moduleRecord.update(newRecord);
        return moduleRecordRepository.saveAndFlush(moduleRecord);
    }

    @Transactional(readOnly = true)
    public List<ModuleRecordDto> listRecordsForPeriod(LocalDate periodStart, LocalDate periodEnd) {
        return moduleRecordRepository
                .findAllByCreatedAtBetweenAndCourseRecordIsNotNullNormalised(periodStart.atStartOfDay(), periodEnd.plusDays(1).atStartOfDay());
    }

    public List<ModuleRecordDto> listRecordsForPeriodAndLearnerIds(LocalDate periodStart, LocalDate periodEnd, List<String> learnerIds) {
        return moduleRecordRepository
                .findForLearnerIdsByCreatedAtBetweenAndCourseRecordIsNotNullNormalised(periodStart.atStartOfDay(), periodEnd.plusDays(1).atStartOfDay(), learnerIds);
    }

    public List<ModuleRecordDto> listRecordsForPeriodAndCourseIds(LocalDate periodStart, LocalDate periodEnd, List<String> courseIds) {
        return moduleRecordRepository
                .findForCourseIdsByCreatedAtBetweenAndCourseRecordIsNotNullNormalised(periodStart.atStartOfDay(), periodEnd.plusDays(1).atStartOfDay(), courseIds);
    }

}
