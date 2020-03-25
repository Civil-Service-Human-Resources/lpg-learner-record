package uk.gov.cslearning.record.api;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cslearning.record.dto.CourseRecordDto;
import uk.gov.cslearning.record.service.CourseRecordService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reporting")
public class CourseRecordController {

    private final CourseRecordService courseRecordService;

    public CourseRecordController(CourseRecordService courseRecordService) {
        this.courseRecordService = courseRecordService;
    }

    @GetMapping(value = "/mandatory-courses", params = {"from", "to"})
    public ResponseEntity<List<CourseRecordDto>> mandatoryCourses(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to

    ) {
        return ResponseEntity.ok(courseRecordService.listRecordsForPeriod(from, to));
    }
}
