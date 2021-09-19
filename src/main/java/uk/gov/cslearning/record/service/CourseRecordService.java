package uk.gov.cslearning.record.service;

import com.github.fge.jsonpatch.JsonPatch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

import uk.gov.cslearning.record.api.input.PATCH.PatchCourseRecordInput;
import uk.gov.cslearning.record.api.mapper.CourseRecordMapper;
import uk.gov.cslearning.record.api.util.PatchHelper;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.exception.CourseRecordNotFoundException;
import uk.gov.cslearning.record.repository.CourseRecordRepository;

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
        courseRecordMapper.update(courseRecord, patchedInput);

        courseRecord.setLastUpdated(LocalDateTime.now());
        return courseRecordRepository.save(courseRecord);
    }

}
