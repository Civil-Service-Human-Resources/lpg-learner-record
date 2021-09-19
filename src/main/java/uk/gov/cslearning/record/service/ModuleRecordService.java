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

        PatchModuleRecordInput patchedInput =  patchHelper.patch(patch, existingRecordAsInput, PatchModuleRecordInput.class);
        moduleRecordMapper.update(moduleRecord, patchedInput);

        LocalDateTime updatedAt = LocalDateTime.now();
        moduleRecord.setUpdatedAt(updatedAt);
        if (hasModuleBeenCompleted(existingRecordAsInput, patchedInput)) {
            moduleRecord.setCompletionDate(updatedAt);
        }
        return moduleRecordRepository.save(moduleRecord);
    }

    public ModuleRecord createModuleRecord(PostModuleRecordInput newModuleInput) {

        String userId = newModuleInput.getUserId();
        String courseId = newModuleInput.getCourseId();
        CourseRecord courseRecord = courseRecordRepository.getCourseRecord(userId, courseId).orElseThrow(() -> new CourseRecordNotFoundException(userId, courseId));
        ModuleRecord newModule = moduleRecordMapper.PostInputAsModule(newModuleInput);
        newModule.setCreatedAt(LocalDateTime.now());
        courseRecord.addModuleRecord(newModule);
        courseRecordRepository.save(courseRecord);
        return newModule;
    }

    private boolean hasModuleBeenCompleted(PatchModuleRecordInput before, PatchModuleRecordInput after) {
        return (State.COMPLETED.toString().equals(after.getState()) &&
                !State.COMPLETED.toString().equals(before.getState()));
    }
}
