package uk.gov.cslearning.record.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.State;
import uk.gov.cslearning.record.service.UserRecordService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.unmodifiableCollection;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/records")
public class LearnerRecordController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LearnerRecordController.class);
    private UserRecordService userRecordService;

    @Value("${xapi.enabled}")
    private boolean learningLockerEnabled;

    @Autowired
    public LearnerRecordController(UserRecordService userRecordService) {
        checkArgument(userRecordService != null);
        this.userRecordService = userRecordService;
    }

    @GetMapping(path = "/{userId}")
    public ResponseEntity<Records> userRecord(@PathVariable("userId") String userId,
                                              @RequestParam(name = "activityId", required = false) List<String> activityIds,
                                              @RequestParam(name = "includeState", required = false) List<State> includeStates,
                                              @RequestParam(name = "ignoreState", required = false) List<State> ignoreStates) {
        LOGGER.debug("Getting user record for {}", userId);
        Collection<CourseRecord> records = new ArrayList<>();
        LOGGER.info("Learning locker enabled: {}", learningLockerEnabled);
        if (learningLockerEnabled) {
            LOGGER.info("Getting records from learning locker.");
            records = userRecordService.getUserRecord(userId, activityIds);
        } else {
            LOGGER.info("Getting records from learner record DB");
            records = userRecordService.getUserRecord(userId);
        }

        if (includeStates != null && !includeStates.isEmpty()) {
            records = records.stream()
                    .filter(courseRecord -> includeStates.contains(courseRecord.getState()))
                    .collect(toList());
        } else if (ignoreStates != null && !ignoreStates.isEmpty()) {
            records = records.stream()
                    .filter(courseRecord -> !ignoreStates.contains(courseRecord.getState()))
                    .collect(toList());
        }

        return new ResponseEntity<>(new Records(records), OK);
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
