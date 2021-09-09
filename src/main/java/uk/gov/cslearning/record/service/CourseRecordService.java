package uk.gov.cslearning.record.service;

import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.client.HttpClientErrorException;
import uk.gov.cslearning.record.api.input.CourseRecordInput;
import uk.gov.cslearning.record.api.mapper.CourseRecordMapper;
import uk.gov.cslearning.record.api.util.PatchHelper;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.exception.CourseRecordNotFoundException;
import uk.gov.cslearning.record.exception.PatchResourceException;
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
        CourseRecordInput updateParams = courseRecordMapper.asInput(courseRecord);

        CourseRecordInput patchedInput =  patchHelper.patch(patch, updateParams, CourseRecordInput.class);
        courseRecordMapper.update(courseRecord, patchedInput);

        courseRecord.setLastUpdated(LocalDateTime.now());
        courseRecordRepository.save(courseRecord);
        return courseRecord;
    }

    public List<CourseRecord> fetchCourseRecords(String userId, String courseId) {
        List<CourseRecord> records = courseRecordRepository.findByUserIdAndCourseId(userId, courseId);
        return records;
    }
}
