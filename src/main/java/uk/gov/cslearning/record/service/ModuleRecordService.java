package uk.gov.cslearning.record.service;

import com.github.fge.jsonpatch.JsonPatch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.api.input.PATCH.PatchModuleRecordInput;
import uk.gov.cslearning.record.api.input.POST.PostModuleRecordInput;
import uk.gov.cslearning.record.api.mapper.ModuleRecordMapper;
import uk.gov.cslearning.record.api.util.PatchHelper;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.domain.State;
import uk.gov.cslearning.record.dto.ModuleRecordDto;
import uk.gov.cslearning.record.exception.CourseRecordNotFoundException;
import uk.gov.cslearning.record.exception.ResourceExists.ModuleRecordAlreadyExistsException;
import uk.gov.cslearning.record.exception.ModuleRecordNotFoundException;
import uk.gov.cslearning.record.repository.CourseRecordRepository;
import uk.gov.cslearning.record.repository.ModuleRecordRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ModuleRecordService {

    private final ModuleRecordRepository moduleRecordRepository;
    private final ModuleRecordMapper moduleRecordMapper;
    private final PatchHelper patchHelper;
    private final CourseRecordRepository courseRecordRepository;

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

    public ModuleRecord updateModuleRecord(Long moduleRecordId, JsonPatch patch) {
        ModuleRecord moduleRecord = moduleRecordRepository.findById(moduleRecordId).orElseThrow(() -> new ModuleRecordNotFoundException(moduleRecordId));
        PatchModuleRecordInput existingRecordAsInput = moduleRecordMapper.asInput(moduleRecord);

        PatchModuleRecordInput patchedInput = patchHelper.patch(patch, existingRecordAsInput, PatchModuleRecordInput.class);
        moduleRecordMapper.update(moduleRecord, patchedInput);

        LocalDateTime updatedAt = LocalDateTime.now();

        if (patch.toString().contains("updatedAt")) {
            updatedAt = patchedInput.getUpdatedAt();
        }

        moduleRecord.setUpdatedAt(updatedAt);
        if (patchedInput.getState().equals(State.COMPLETED.toString())) {
            moduleRecord.setCompletionDate(updatedAt);
        }
        CourseRecord cr = moduleRecord.getCourseRecord();
        cr.setLastUpdated(updatedAt);
        courseRecordRepository.save(cr);
        return moduleRecordRepository.save(moduleRecord);
    }

    public ModuleRecord createModuleRecord(PostModuleRecordInput newModuleInput) {

        String userId = newModuleInput.getUserId();
        String courseId = newModuleInput.getCourseId();
        String moduleId = newModuleInput.getModuleId();
        CourseRecord courseRecord = courseRecordRepository.getCourseRecord(userId, courseId).orElseThrow(() -> new CourseRecordNotFoundException(userId, courseId));
        moduleRecordRepository.findModuleRecordByModuleIdAndCourseRecordIdentityCourseIdAndCourseRecordIdentityUserId(moduleId, courseId, userId).ifPresent(mr -> {throw new ModuleRecordAlreadyExistsException(courseId, moduleId, userId);});
        ModuleRecord newModule = moduleRecordMapper.postInputAsModule(newModuleInput);
        courseRecord.addModuleRecord(newModule);
        CourseRecord updatedRecord = courseRecordRepository.save(courseRecord);
        return updatedRecord.getModuleRecord(moduleId);
    }

}
