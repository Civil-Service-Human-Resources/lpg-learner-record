package uk.gov.cslearning.record.api;

import com.github.fge.jsonpatch.JsonPatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.cslearning.record.api.output.CourseRecordOutput;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.service.CourseRecordService;

import java.util.List;

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

    @GetMapping
    public ResponseEntity<CourseRecordOutput> fetchCourseRecords(@RequestParam String userId,
                                                                 @RequestParam(required = false) String courseId) {

        List<CourseRecord> courseRecords = courseRecordService.fetchCourseRecords(userId, courseId);
        CourseRecordOutput responseObject = new CourseRecordOutput(courseRecords);
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }
}
