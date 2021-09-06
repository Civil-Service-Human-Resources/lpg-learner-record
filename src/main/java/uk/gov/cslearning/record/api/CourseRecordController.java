package uk.gov.cslearning.record.api;

import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.service.CourseRecordService;

@Slf4j
@RestController
@RequestMapping("/course_records")
public class CourseRecordController {

    private final CourseRecordService courseRecordService;

    public CourseRecordController(CourseRecordService courseRecordService) {
        this.courseRecordService = courseRecordService;
    }

    @PatchMapping(consumes = "application/json-patch+json")
    public ResponseEntity<CourseRecord> updateCourseRecord(@RequestParam String userId,
                                                           @RequestParam String courseId,
                                                           @RequestBody JsonPatch patchData) {
        CourseRecord updatedRecord = courseRecordService.updateCourseRecord(userId, courseId, patchData);
        return new ResponseEntity<>(updatedRecord, HttpStatus.OK);
    }
}
