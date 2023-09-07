package uk.gov.cslearning.record.service;

import com.github.fge.jsonpatch.JsonPatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.api.input.PATCH.PatchCourseRecordInput;
import uk.gov.cslearning.record.api.input.POST.PostCourseRecordInput;
import uk.gov.cslearning.record.api.mapper.CourseRecordMapper;
import uk.gov.cslearning.record.api.util.PatchHelper;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.exception.CourseRecordNotFoundException;
import uk.gov.cslearning.record.repository.CourseRecordRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseRecordService {

    private final CourseRecordRepository courseRecordRepository;
    private final CourseRecordMapper courseRecordMapper;
    private final PatchHelper patchHelper;

    public CourseRecord updateCourseRecord(String userId, String courseId, JsonPatch patch) {
        CourseRecord courseRecord = courseRecordRepository.getCourseRecord(userId, courseId).orElseThrow(() -> new CourseRecordNotFoundException(userId, courseId));
        PatchCourseRecordInput updateParams = courseRecordMapper.asInput(courseRecord);

        PatchCourseRecordInput patchedInput = patchHelper.patch(patch, updateParams, PatchCourseRecordInput.class);
        courseRecordMapper.update(courseRecord, patchedInput);
        return courseRecordRepository.save(courseRecord);
    }

    public List<CourseRecord> fetchCourseRecords(String userId, List<String> courseIds) {
        if (CollectionUtils.isEmpty(courseIds)) {
            log.info(String.format("Fetching all course records for user '%s'", userId));
            return courseRecordRepository.findByUserId(userId);
        } else {
            log.info(String.format("Fetching all course records for user '%s' and course IDs '%s'", userId, courseIds));
            return courseRecordRepository.findByUserIdAndCourseIdIn(userId, courseIds);
        }
    }

    public CourseRecord createCourseRecord(PostCourseRecordInput inputCourse) {
        CourseRecord newCourseRecord = courseRecordMapper.postInputAsCourseRecord(inputCourse);
        return courseRecordRepository.save(newCourseRecord);
    }

}
