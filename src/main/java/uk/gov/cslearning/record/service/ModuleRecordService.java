package uk.gov.cslearning.record.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.api.FetchModuleRecordParams;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.domain.record.LearnerRecordTypeEnum;
import uk.gov.cslearning.record.dto.ModuleRecordDto;
import uk.gov.cslearning.record.exception.ModuleRecordNotFoundException;
import uk.gov.cslearning.record.exception.ResourceExists.ModuleRecordAlreadyExistsException;
import uk.gov.cslearning.record.repository.CourseRecordRepository;
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
    private final LearnerRecordService learnerRecordService;
    private final CourseRecordRepository courseRecordRepository;
    private final IUtilService utilService;

    public ModuleRecord createModuleRecord(ModuleRecord input) {
        if (moduleRecordRepository.getModuleRecord(input.getUserId(), input.getModuleId()).isPresent()) {
            throw new ModuleRecordAlreadyExistsException(input.getModuleId(), input.getUserId());
        }
        LocalDateTime createdTimestamp = utilService.getNowDateTime();
        // Creating the legacy course record is still required until we decommission the course_record table.
        CourseRecord courseRecord = courseRecordRepository.getCourseRecord(input.getUserId(), input.getCourseId()).orElseGet(() -> {
            CourseRecord cr = new CourseRecord(input.getCourseId(), input.getUserId());
            cr.setCourseTitle(input.getCourseTitle());
            return cr;
        });
        courseRecord.setLastUpdated(createdTimestamp);
        // Creating the new style course record is still required until we fully migrate the new learner record logic.
        learnerRecordService.createRecordIfNotExists(input.getCourseId(), input.getUserId(), LearnerRecordTypeEnum.COURSE.name(), createdTimestamp);
        input.setCreatedAt(createdTimestamp);
        input.setUid(utilService.generateUUID());
        input.setUpdatedAt(createdTimestamp);
        input.setCourseRecord(courseRecord);
        courseRecord.addModuleRecord(input);
//        moduleRecordRepository.saveAndFlush(input);
        courseRecordRepository.save(courseRecord);
        return input;
    }

    public ModuleRecord updateModuleRecord(Long moduleRecordId, ModuleRecord newRecord) {
        LocalDateTime updated = utilService.getNowDateTime();
        ModuleRecord moduleRecord = moduleRecordRepository.findById(moduleRecordId).orElseThrow(() -> new ModuleRecordNotFoundException(moduleRecordId));
        newRecord.setUpdatedAt(updated);
        if (StringUtils.isBlank(moduleRecord.getUid())) {
            moduleRecord.setUid(utilService.generateUUID());
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

    public List<ModuleRecord> fetchModuleRecords(FetchModuleRecordParams fetchModuleRecordParams) {
        return moduleRecordRepository.findByUserIdAndModuleIdIn(fetchModuleRecordParams.getUserIds(), fetchModuleRecordParams.getModuleIds());
    }
}
