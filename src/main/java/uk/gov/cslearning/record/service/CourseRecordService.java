package uk.gov.cslearning.record.service;

import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import uk.gov.cslearning.record.api.input.CourseRecordInput;
import uk.gov.cslearning.record.api.mapper.CourseRecordMapper;
import uk.gov.cslearning.record.api.util.PatchHelper;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.exception.CourseRecordNotFoundException;
import uk.gov.cslearning.record.exception.PatchResourceException;
import uk.gov.cslearning.record.repository.CourseRecordRepository;

@Service
@RequiredArgsConstructor
public class CourseRecordService {

    private final CourseRecordRepository courseRecordRepository;
    private final CourseRecordMapper courseRecordMapper;
    private final PatchHelper patchHelper;

    public CourseRecord updateCourseRecord(String userId, String courseId, JsonPatch patch) {
        CourseRecord courseRecord = courseRecordRepository.getCourseRecord(userId, courseId).orElseThrow(() -> new CourseRecordNotFoundException(userId, courseId));
        CourseRecordInput updateParams = courseRecordMapper.asInput(courseRecord);

        CourseRecordInput patchedInput = null;
        try {
            patchedInput = patchHelper.patch(patch, updateParams, CourseRecordInput.class);
        } catch (JsonPatchException e) {
            throw new PatchResourceException(e.getMessage());
        }

        courseRecordMapper.update(courseRecord, patchedInput);
        courseRecordRepository.save(courseRecord);
        return courseRecord;
    }

//    public CourseRecord findCourseRecord(String userId, String courseId) {
//        return courseRecordRepository.getCourseRecord(userId, courseId).orElseThrow(() -> new CourseRecordNotFoundException(userId, courseId));
//    }

}
