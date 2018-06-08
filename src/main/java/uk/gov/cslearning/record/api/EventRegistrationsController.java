package uk.gov.cslearning.record.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cslearning.record.repository.CourseRecordRepository;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
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
    public ResponseEntity<List<Count>> activityRecord(@RequestParam("eventId") String[] eventIds) {
        List<Count> counts = new ArrayList<>();
        for (String eventId : eventIds) {
            Integer count = courseRecordRepository.countRegisteredForEvent(eventId);
            counts.add(new Count(eventId, count));
        }
        return new ResponseEntity<>(counts, OK);
    }

    public static final class Count {

        private String eventId;

        private int value;

        public Count(String eventId, Integer value) {
            this.eventId = eventId;
            if (value != null) {
                this.value = value;
            }
        }

        public String getEventId() {
            return eventId;
        }

        public int getValue() {
            return value;
        }
    }
}
