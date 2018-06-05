package uk.gov.cslearning.record.api;

import org.h2.util.New;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.service.ActivityRecordService;
import uk.gov.cslearning.record.service.UserRecordService;
import uk.gov.cslearning.record.service.scheduler.LearningJob;

import java.util.ArrayList;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.unmodifiableCollection;
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
        Collection<CourseRecord> records = activityRecordService.getActivityRecord(activityId);
        return new ResponseEntity<>(new Records(records), OK);
    }

    @GetMapping(path = "/{userId}")
    public ResponseEntity<Records> userRecord(@PathVariable("userId") String userId,
                                              @RequestParam(name = "activityId", required = false) String activityId) {
        Collection<CourseRecord> records = userRecordService.getUserRecord(userId, activityId);
        return new ResponseEntity<>(new Records(records), OK);
    }

    @Autowired
    private LearningJob learningJob;

    @GetMapping(path = "/notify")
    public ResponseEntity notifyme() {
        try {
            learningJob.sendNotificationForCompletedLearning();
        } catch (Exception e) {
            System.out.println(e);

        }

        return ResponseEntity.ok().build();
    }

    public static final class Records {

        private Collection<CourseRecord> records;

        public Records(Collection<CourseRecord> records) {
            checkArgument(records != null, "records is null");
            this.records = new ArrayList<>(records);
        }

        public Collection<CourseRecord> getRecords() {
            return unmodifiableCollection(records);
        }
    }
}
