package uk.gov.cslearning.record.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.repository.CourseRecordRepository;
import uk.gov.cslearning.record.service.ActivityRecordService;
import uk.gov.cslearning.record.service.UserRecordService;

import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.unmodifiableCollection;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/registrations")
public class EventRegistrationsController {

    private CourseRecordRepository courseRecordRepository;

    @Autowired
    public EventRegistrationsController(CourseRecordRepository courseRecordRepository) {
        checkArgument(courseRecordRepository != null);
        this.courseRecordRepository = courseRecordRepository;
    }

    @GetMapping("/count")
    public ResponseEntity<Count> activityRecord(@QueryParam("eventId") String eventId) {
        Integer count = courseRecordRepository.countRegisteredForEvent(eventId);
        return new ResponseEntity<>(new Count(count), OK);
    }

    public static final class Count {

        private int value;

        public Count(Integer value) {
            if (value != null) {
                this.value = value;
            }
        }

        public int getValue() {
            return value;
        }
    }
}
