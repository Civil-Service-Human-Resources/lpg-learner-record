package uk.gov.cslearning.record.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.api.FromToParams;
import uk.gov.cslearning.record.api.FromToParamsCourseIds;
import uk.gov.cslearning.record.api.FromToParamsUserIds;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.dto.ModuleRecordDto;
import uk.gov.cslearning.record.exception.ModuleRecordNotFoundException;
import uk.gov.cslearning.record.repository.ModuleRecordRepository;
import uk.gov.cslearning.record.util.IUtilService;

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
        input.setCourseRecord(parentRecord);
        return moduleRecordRepository.saveAndFlush(input);
    }

    public ModuleRecord updateModuleRecord(Long moduleRecordId, ModuleRecord newRecord, LocalDateTime updated) {
        ModuleRecord moduleRecord = moduleRecordRepository.findById(moduleRecordId).orElseThrow(() -> new ModuleRecordNotFoundException(moduleRecordId));
        newRecord.setUpdatedAt(updated);
        if (StringUtils.isBlank(moduleRecord.getUid())) {
            moduleRecord.setUid(utilService.generateUUID());
        }
        moduleRecord.update(newRecord);
        return moduleRecordRepository.saveAndFlush(moduleRecord);
    }

    private ModuleRecordDto create(ModuleRecord moduleRecord) {
        return new ModuleRecordDto(moduleRecord.getUid(), moduleRecord.getModuleId(), moduleRecord.getState(), moduleRecord.getCourseRecord().getUserId(),
                moduleRecord.getUpdatedAt(), moduleRecord.getCompletionDate(), moduleRecord.getModuleTitle(), moduleRecord.getModuleType(),
                moduleRecord.getCourseRecord().getCourseId(), moduleRecord.getCourseRecord().getCourseTitle());
    }

    @Transactional(readOnly = true)
    public List<ModuleRecordDto> listRecordsForPeriod(FromToParams params) {
        return moduleRecordRepository
                .findAllByUpdatedAtBetween(params.getFrom().atStartOfDay(), params.getTo().plusDays(1).atStartOfDay())
                .stream().map(this::create).toList();
    }

    public List<ModuleRecordDto> listRecordsForPeriodAndLearnerIds(FromToParamsUserIds params) {
        return moduleRecordRepository
                .findAllByUpdatedAtBetweenAndCourseRecord_Identity_UserIdInOrderByCourseRecord_Identity_UserId(params.getFrom().atStartOfDay(), params.getTo().plusDays(1).atStartOfDay(), params.getLearnerIds())
                .stream().map(this::create).toList();
    }

    public List<ModuleRecordDto> listRecordsForPeriodAndCourseIds(FromToParamsCourseIds params) {
        return moduleRecordRepository
                .findAllByUpdatedAtBetweenAndCourseRecord_Identity_CourseIdInOrderByCourseRecord_Identity_UserId(params.getFrom().atStartOfDay(), params.getTo().plusDays(1).atStartOfDay(), params.getCourseIds())
                .stream().map(this::create).toList();
    }

}
