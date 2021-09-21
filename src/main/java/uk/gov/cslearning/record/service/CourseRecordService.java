package uk.gov.cslearning.record.service;

import com.github.fge.jsonpatch.JsonPatch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

import uk.gov.cslearning.record.api.input.PATCH.PatchCourseRecordInput;
import uk.gov.cslearning.record.api.input.POST.PostCourseRecordInput;
import uk.gov.cslearning.record.api.mapper.CourseRecordMapper;
import uk.gov.cslearning.record.api.util.PatchHelper;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.exception.CourseRecordNotFoundException;
import uk.gov.cslearning.record.exception.ResourceExists.CourseRecordAlreadyExistsException;
import uk.gov.cslearning.record.exception.ResourceExists.ModuleRecordAlreadyExistsException;
import uk.gov.cslearning.record.repository.CourseRecordRepository;
import uk.gov.cslearning.record.repository.ModuleRecordRepository;

@Service
@RequiredArgsConstructor
public class CourseRecordService {

    private final CourseRecordRepository courseRecordRepository;
    private final ModuleRecordRepository moduleRecordRepository;
    private final CourseRecordMapper courseRecordMapper;
    private final PatchHelper patchHelper;

    public CourseRecord updateCourseRecord(String userId, String courseId, JsonPatch patch) {
        CourseRecord courseRecord = courseRecordRepository.getCourseRecord(userId, courseId).orElseThrow(() -> new CourseRecordNotFoundException(userId, courseId));
        PatchCourseRecordInput updateParams = courseRecordMapper.asInput(courseRecord);

        PatchCourseRecordInput patchedInput =  patchHelper.patch(patch, updateParams, PatchCourseRecordInput.class);
        courseRecordMapper.update(courseRecord, patchedInput);

        courseRecord.setLastUpdated(LocalDateTime.now());
        return courseRecordRepository.save(courseRecord);
    }

    public List<CourseRecord> fetchCourseRecords(String userId, String courseId) {
        List<CourseRecord> records = courseRecordRepository.findByUserIdAndCourseId(userId, courseId);
        return records;
    }

    public CourseRecord createCourseRecord(PostCourseRecordInput inputCourse) {
        String userId = inputCourse.getUserId();
        String courseId = inputCourse.getCourseId();
        courseRecordRepository.getCourseRecord(userId, courseId).ifPresent(cr -> {throw new CourseRecordAlreadyExistsException(courseId, userId);});

        CourseRecord newCourseRecord = courseRecordMapper.postInputAsCourseRecord(inputCourse);

        return courseRecordRepository.save(newCourseRecord);
    }

    private void validateNewCourseRecord(PostCourseRecordInput newCourseRecord) {

    }
}
