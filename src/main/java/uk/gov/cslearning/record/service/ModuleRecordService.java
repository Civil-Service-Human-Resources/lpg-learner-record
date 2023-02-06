package uk.gov.cslearning.record.service;

import com.github.fge.jsonpatch.JsonPatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.api.input.PATCH.PatchModuleRecordInput;
import uk.gov.cslearning.record.api.input.POST.PostModuleRecordInput;
import uk.gov.cslearning.record.api.mapper.ModuleRecordMapper;
import uk.gov.cslearning.record.api.util.PatchHelper;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.dto.ModuleRecordDto;
import uk.gov.cslearning.record.exception.CourseRecordNotFoundException;
import uk.gov.cslearning.record.exception.ResourceExists.ModuleRecordAlreadyExistsException;
import uk.gov.cslearning.record.exception.ModuleRecordNotFoundException;
import uk.gov.cslearning.record.repository.CourseRecordRepository;
import uk.gov.cslearning.record.repository.ModuleRecordRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
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
        log.debug("LC-1627: ModuleRecordService.updateModuleRecord.received moduleRecordId: {} ", moduleRecordId);
        log.debug("LC-1627: ModuleRecordService.updateModuleRecord.received patch: {} ", patch);

        ModuleRecord moduleRecord = moduleRecordRepository.findById(moduleRecordId).orElseThrow(() -> new ModuleRecordNotFoundException(moduleRecordId));
        log.debug("LC-1627: ModuleRecordService.updateModuleRecord. existing moduleRecord: {} ", moduleRecord);
        log.debug("LC-1627: ModuleRecordService.updateModuleRecord. existing moduleRecord.getId: {} ", moduleRecord.getId());
        log.debug("LC-1627: ModuleRecordService.updateModuleRecord. existing moduleRecord.getModuleId: {} ", moduleRecord.getModuleId());
        log.debug("LC-1627: ModuleRecordService.updateModuleRecord. existing moduleRecord.getCreatedAt: {} ", moduleRecord.getCreatedAt());
        log.debug("LC-1627: ModuleRecordService.updateModuleRecord. existing moduleRecord.getUpdatedAt: {} ", moduleRecord.getUpdatedAt());
        log.debug("LC-1627: ModuleRecordService.updateModuleRecord. existing moduleRecord.getCompletionDate: {} ", moduleRecord.getCompletionDate());
        log.debug("LC-1627: ModuleRecordService.updateModuleRecord. existing moduleRecord.getState: {} ", moduleRecord.getState());
        log.debug("LC-1627: ModuleRecordService.updateModuleRecord. existing moduleRecord.getResult: {} ", moduleRecord.getResult());
        log.debug("LC-1627: ModuleRecordService.updateModuleRecord. existing moduleRecord.getScore:: {} ", moduleRecord.getScore());
        log.debug("LC-1627: ModuleRecordService.updateModuleRecord. existing moduleRecord.getModuleTitle: {} ", moduleRecord.getModuleTitle());

        PatchModuleRecordInput existingRecordAsInput = moduleRecordMapper.asInput(moduleRecord);
        log.debug("LC-1627: ModuleRecordService.updateModuleRecord. existingRecordAsInput: {} ", existingRecordAsInput);
        log.debug("LC-1627: ModuleRecordService.updateModuleRecord. existingRecordAsInput.getUid: {} ", existingRecordAsInput.getUid());
        log.debug("LC-1627: ModuleRecordService.updateModuleRecord. existingRecordAsInput.getUpdatedAt: {} ", existingRecordAsInput.getUpdatedAt());
        log.debug("LC-1627: ModuleRecordService.updateModuleRecord. existingRecordAsInput.getCompletionDate: {} ", existingRecordAsInput.getCompletionDate());
        log.debug("LC-1627: ModuleRecordService.updateModuleRecord. existingRecordAsInput.getState: {} ", existingRecordAsInput.getState());
        log.debug("LC-1627: ModuleRecordService.updateModuleRecord. existingRecordAsInput.getResult: {} ", existingRecordAsInput.getResult());
        log.debug("LC-1627: ModuleRecordService.updateModuleRecord. existingRecordAsInput.getScore: {} ", existingRecordAsInput.getScore());

        PatchModuleRecordInput patchedInput = patchHelper.patch(patch, existingRecordAsInput, PatchModuleRecordInput.class);
        log.debug("LC-1627: ModuleRecordService.updateModuleRecord. patchedInput: {} ", patchedInput);
        log.debug("LC-1627: ModuleRecordService.updateModuleRecord. patchedInput.getUid: {} ", patchedInput.getUid());
        log.debug("LC-1627: ModuleRecordService.updateModuleRecord. patchedInput.getUpdatedAt: {} ", patchedInput.getUpdatedAt());
        log.debug("LC-1627: ModuleRecordService.updateModuleRecord. patchedInput.getCompletionDate: {} ", patchedInput.getCompletionDate());
        log.debug("LC-1627: ModuleRecordService.updateModuleRecord. patchedInput.getState: {} ", patchedInput.getState());
        log.debug("LC-1627: ModuleRecordService.updateModuleRecord. patchedInput.getResult: {} ", patchedInput.getResult());
        log.debug("LC-1627: ModuleRecordService.updateModuleRecord. patchedInput.getScore: {} ", patchedInput.getScore());

        moduleRecordMapper.update(moduleRecord, patchedInput);
        log.debug("LC-1627: ModuleRecordService.updateModuleRecord. before save updated moduleRecord: {} ", moduleRecord);
        log.debug("LC-1627: ModuleRecordService.updateModuleRecord. before save updated moduleRecord.getId: {} ", moduleRecord.getId());
        log.debug("LC-1627: ModuleRecordService.updateModuleRecord. before save updated moduleRecord.getModuleId: {} ", moduleRecord.getModuleId());
        log.debug("LC-1627: ModuleRecordService.updateModuleRecord. before save updated moduleRecord.getCreatedAt: {} ", moduleRecord.getCreatedAt());
        log.debug("LC-1627: ModuleRecordService.updateModuleRecord. before save updated moduleRecord.getUpdatedAt: {} ", moduleRecord.getUpdatedAt());
        log.debug("LC-1627: ModuleRecordService.updateModuleRecord. before save updated moduleRecord.getCompletionDate: {} ", moduleRecord.getCompletionDate());
        log.debug("LC-1627: ModuleRecordService.updateModuleRecord. before save updated moduleRecord.getState: {} ", moduleRecord.getState());
        log.debug("LC-1627: ModuleRecordService.updateModuleRecord. before save updated moduleRecord.getResult: {} ", moduleRecord.getResult());
        log.debug("LC-1627: ModuleRecordService.updateModuleRecord. before save updated moduleRecord.getScore: {} ", moduleRecord.getScore());
        log.debug("LC-1627: ModuleRecordService.updateModuleRecord. before save updated moduleRecord.getModuleTitle: {} ", moduleRecord.getModuleTitle());

        CourseRecord cr = moduleRecord.getCourseRecord();
        cr.setLastUpdated(moduleRecord.getUpdatedAt());
        courseRecordRepository.save(cr);

        ModuleRecord savedModuleRecord = moduleRecordRepository.save(moduleRecord);
        log.debug("LC-1627: ModuleRecordService.updateModuleRecord. savedModuleRecord: {} ", savedModuleRecord);
        log.debug("LC-1627: ModuleRecordService.updateModuleRecord. savedModuleRecord.getId: {} ", savedModuleRecord.getId());
        log.debug("LC-1627: ModuleRecordService.updateModuleRecord. savedModuleRecord.getModuleId: {} ", savedModuleRecord.getModuleId());
        log.debug("LC-1627: ModuleRecordService.updateModuleRecord. savedModuleRecord.getCreatedAt: {} ", savedModuleRecord.getCreatedAt());
        log.debug("LC-1627: ModuleRecordService.updateModuleRecord. savedModuleRecord.getUpdatedAt: {} ", savedModuleRecord.getUpdatedAt());
        log.debug("LC-1627: ModuleRecordService.updateModuleRecord. savedModuleRecord.getCompletionDate: {} ", savedModuleRecord.getCompletionDate());
        log.debug("LC-1627: ModuleRecordService.updateModuleRecord. savedModuleRecord.getState: {} ", savedModuleRecord.getState());
        log.debug("LC-1627: ModuleRecordService.updateModuleRecord. savedModuleRecord.getResult: {} ", savedModuleRecord.getResult());
        log.debug("LC-1627: ModuleRecordService.updateModuleRecord. savedModuleRecord.getScore: {} ", savedModuleRecord.getScore());
        log.debug("LC-1627: ModuleRecordService.updateModuleRecord. savedModuleRecord.getModuleTitle: {} ", savedModuleRecord.getModuleTitle());

        return savedModuleRecord;
    }

    public ModuleRecord createModuleRecord(PostModuleRecordInput newModuleInput) {

        String uid = newModuleInput.getUid();
        String userId = newModuleInput.getUserId();
        String courseId = newModuleInput.getCourseId();
        String moduleId = newModuleInput.getModuleId();

        log.debug("LC-1627: ModuleRecordService.createModuleRecord.newModuleInput.getUid: {} ", uid);
        log.debug("LC-1627: ModuleRecordService.createModuleRecord.newModuleInput.getUserId: {} ", userId);
        log.debug("LC-1627: ModuleRecordService.createModuleRecord.newModuleInput.getCourseId: {} ", courseId);
        log.debug("LC-1627: ModuleRecordService.createModuleRecord.newModuleInput.getModuleId: {} ", moduleId);
        log.debug("LC-1627: ModuleRecordService.createModuleRecord.newModuleInput.getModuleTitle: {} ", newModuleInput.getModuleTitle());
        log.debug("LC-1627: ModuleRecordService.createModuleRecord.newModuleInput.getState: {} ", newModuleInput.getState());

        CourseRecord courseRecord = courseRecordRepository.getCourseRecord(userId, courseId).orElseThrow(() -> new CourseRecordNotFoundException(userId, courseId));
        moduleRecordRepository.findModuleRecordByModuleIdAndCourseRecordIdentityCourseIdAndCourseRecordIdentityUserId(moduleId, courseId, userId).ifPresent(mr -> {throw new ModuleRecordAlreadyExistsException(courseId, moduleId, userId);});

        ModuleRecord newModule = moduleRecordMapper.postInputAsModule(newModuleInput);
        log.debug("LC-1627: ModuleRecordService.createModuleRecord. before save updated newModule: {} ", newModule);
        log.debug("LC-1627: ModuleRecordService.createModuleRecord. before save updated newModule.getId: {} ", newModule.getId());
        log.debug("LC-1627: ModuleRecordService.createModuleRecord. before save updated newModule.getModuleId: {} ", newModule.getModuleId());
        log.debug("LC-1627: ModuleRecordService.createModuleRecord. before save updated newModule.getCreatedAt: {} ", newModule.getCreatedAt());
        log.debug("LC-1627: ModuleRecordService.createModuleRecord. before save updated newModule.getUpdatedAt: {} ", newModule.getUpdatedAt());
        log.debug("LC-1627: ModuleRecordService.createModuleRecord. before save updated newModule.getCompletionDate: {} ", newModule.getCompletionDate());
        log.debug("LC-1627: ModuleRecordService.createModuleRecord. before save updated newModule.getState: {} ", newModule.getState());
        log.debug("LC-1627: ModuleRecordService.createModuleRecord. before save updated newModule.getResult: {} ", newModule.getResult());
        log.debug("LC-1627: ModuleRecordService.createModuleRecord. before save updated newModule.getScore: {} ", newModule.getScore());
        log.debug("LC-1627: ModuleRecordService.createModuleRecord. before save updated newModule.getModuleTitle: {} ", newModule.getModuleTitle());

        courseRecord.addModuleRecord(newModule);
        CourseRecord updatedRecord = courseRecordRepository.save(courseRecord);

        ModuleRecord savedModuleRecord = updatedRecord.getModuleRecord(moduleId);
        log.debug("LC-1627: ModuleRecordService.createModuleRecord. savedModuleRecord: {} ", savedModuleRecord);
        log.debug("LC-1627: ModuleRecordService.createModuleRecord. savedModuleRecord.getId: {} ", savedModuleRecord.getId());
        log.debug("LC-1627: ModuleRecordService.createModuleRecord. savedModuleRecord.getModuleId: {} ", savedModuleRecord.getModuleId());
        log.debug("LC-1627: ModuleRecordService.createModuleRecord. savedModuleRecord.getCreatedAt: {} ", savedModuleRecord.getCreatedAt());
        log.debug("LC-1627: ModuleRecordService.createModuleRecord. savedModuleRecord.getUpdatedAt: {} ", savedModuleRecord.getUpdatedAt());
        log.debug("LC-1627: ModuleRecordService.createModuleRecord. savedModuleRecord.getCompletionDate: {} ", savedModuleRecord.getCompletionDate());
        log.debug("LC-1627: ModuleRecordService.createModuleRecord. savedModuleRecord.getState: {} ", savedModuleRecord.getState());
        log.debug("LC-1627: ModuleRecordService.createModuleRecord. savedModuleRecord.getResult: {} ", savedModuleRecord.getResult());
        log.debug("LC-1627: ModuleRecordService.createModuleRecord. savedModuleRecord.getScore: {} ", savedModuleRecord.getScore());
        log.debug("LC-1627: ModuleRecordService.createModuleRecord. savedModuleRecord.getModuleTitle: {} ", savedModuleRecord.getModuleTitle());

        return savedModuleRecord;
    }
}
