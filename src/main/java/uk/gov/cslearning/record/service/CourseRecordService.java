package uk.gov.cslearning.record.service;

import com.github.fge.jsonpatch.JsonPatch;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.api.input.PATCH.PatchCourseRecordInput;
import uk.gov.cslearning.record.api.input.POST.PostCourseRecordInput;
import uk.gov.cslearning.record.api.mapper.CourseRecordMapper;
import uk.gov.cslearning.record.api.util.PatchHelper;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.CourseRecordIdentity;
import uk.gov.cslearning.record.exception.CourseRecordNotFoundException;
import uk.gov.cslearning.record.exception.ResourceExists.CourseRecordAlreadyExistsException;
import uk.gov.cslearning.record.repository.CourseRecordRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseRecordService {

    private final CourseRecordRepository courseRecordRepository;
    private final CourseRecordMapper courseRecordMapper;
    private final PatchHelper patchHelper;

    public CourseRecord updateCourseRecord(String userId, String courseId, JsonPatch patch) {
        CourseRecord courseRecord = courseRecordRepository.getCourseRecord(userId, courseId).orElseThrow(() -> new CourseRecordNotFoundException(userId, courseId));
        PatchCourseRecordInput updateParams = courseRecordMapper.asInput(courseRecord);

        PatchCourseRecordInput patchedInput =  patchHelper.patch(patch, updateParams, PatchCourseRecordInput.class);
        patchedInput.setLastUpdated(LocalDateTime.now());
        courseRecordMapper.update(courseRecord, patchedInput);
        return courseRecordRepository.save(courseRecord);
    }

    public List<CourseRecord> fetchCourseRecords(String userId, List<String> courseIds) {
        if (CollectionUtils.isEmpty(courseIds)) {
            return courseRecordRepository.findByUserId(userId);
        } else {
            return courseRecordRepository.findByUserIdAndCourseIdIn(userId, courseIds);
        }
    }

    public CourseRecord createCourseRecord(PostCourseRecordInput inputCourse) {
        String userId = inputCourse.getUserId();
        String courseId = inputCourse.getCourseId();
        courseRecordRepository.getCourseRecord(userId, courseId).ifPresent(cr -> {throw new CourseRecordAlreadyExistsException(courseId, userId);});

        CourseRecord newCourseRecord = courseRecordMapper.postInputAsCourseRecord(inputCourse);

        return courseRecordRepository.save(newCourseRecord);
    }

}
