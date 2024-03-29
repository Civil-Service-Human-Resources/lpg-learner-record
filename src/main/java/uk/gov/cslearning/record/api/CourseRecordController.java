package uk.gov.cslearning.record.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.cslearning.record.api.output.CourseRecordOutput;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.service.CourseRecordService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/course_records")
public class CourseRecordController {

    private final CourseRecordService courseRecordService;

    public CourseRecordController(CourseRecordService courseRecordService) {
        this.courseRecordService = courseRecordService;
    }

    @PostMapping
    public ResponseEntity<CourseRecord> createCourseRecord(@Valid @RequestBody CourseRecord inputCourse) {
        log.debug("Creating course record");
        CourseRecord newRecord = courseRecordService.createCourseRecord(inputCourse);
        return new ResponseEntity<>(newRecord, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<CourseRecordOutput> fetchCourseRecords(@RequestParam String userId,
                                                                 @RequestParam(required = false) List<String> courseIds) {

        List<CourseRecord> courseRecords = courseRecordService.fetchCourseRecords(userId, courseIds);
        CourseRecordOutput responseObject = new CourseRecordOutput(courseRecords);
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    @PutMapping
    @ResponseBody
    public CourseRecord updateCourseRecord(@Valid @RequestBody CourseRecord courseRecord) {
        return courseRecordService.updateCourseRecord(courseRecord);
    }
}
