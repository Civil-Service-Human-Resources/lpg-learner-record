package uk.gov.cslearning.record.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

    @PostMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public CourseRecord createCourseRecord(@Valid @RequestBody CourseRecord inputCourse) {
        return courseRecordService.createCourseRecord(inputCourse);
    }

    @PostMapping("/bulk")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public CourseRecordOutput createCourseRecords(@Valid @RequestBody @Size(min = 1, max = 5) List<CourseRecord> inputCourses) {
        return new CourseRecordOutput(inputCourses.stream().map(courseRecordService::createCourseRecord).toList());
    }

    @GetMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public CourseRecordOutput fetchCourseRecords(@Valid FetchCourseRecordParams fetchCourseRecordParams) {
        List<CourseRecord> courseRecords = courseRecordService.fetchCourseRecords(fetchCourseRecordParams);
        return new CourseRecordOutput(courseRecords);
    }

    @PutMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public CourseRecord updateCourseRecord(@Valid @RequestBody CourseRecord courseRecord) {
        return courseRecordService.updateCourseRecord(courseRecord);
    }

    @PutMapping("/bulk")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public CourseRecordOutput updateCourseRecords(@Valid @RequestBody @Size(min = 1, max = 5) List<CourseRecord> courseRecords) {
        return new CourseRecordOutput(courseRecords.stream().map(courseRecordService::updateCourseRecord).toList());
    }
}
