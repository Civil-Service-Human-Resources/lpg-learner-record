package uk.gov.cslearning.record.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.cslearning.record.domain.Record;
import uk.gov.cslearning.record.domain.State;
import uk.gov.cslearning.record.service.ActivityRecordService;
import uk.gov.cslearning.record.service.UserRecordService;

import java.util.ArrayList;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.unmodifiableCollection;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/records")
public class LearnerRecordController {

    private ActivityRecordService activityRecordService;

    private UserRecordService userRecordService;

    @Autowired
    public LearnerRecordController(ActivityRecordService activityRecordService, UserRecordService userRecordService) {
        checkArgument(activityRecordService != null);
        checkArgument(userRecordService != null);
        this.activityRecordService = activityRecordService;
        this.userRecordService = userRecordService;
    }

    @GetMapping
    public ResponseEntity<Records> activityRecord(@RequestParam(name = "activityId") String activityId) {
        Collection<Record> records = activityRecordService.getActivityRecord(activityId);
        return new ResponseEntity<>(new Records(records), OK);
    }

    @GetMapping(path = "/{userId}")
    public ResponseEntity<Records> userRecord(@PathVariable("userId") String userId,
                                              @RequestParam(name = "activityId", required = false) String activityId,
                                              @RequestParam(name = "state", required = false) State state) {

        Collection<Record> records = userRecordService.getUserRecord(userId, activityId);

        if (state != null) {
            records = records.stream()
                    .filter(record -> state.equals(record.getState()))
                    .collect(toList());
        }

        return new ResponseEntity<>(new Records(records), OK);
    }

    public static final class Records {

        private Collection<Record> records;

        public Records(Collection<Record> records) {
            checkArgument(records != null, "records is null");
            this.records = new ArrayList<>(records);
        }

        public Collection<Record> getRecords() {
            return unmodifiableCollection(records);
        }
    }
}
