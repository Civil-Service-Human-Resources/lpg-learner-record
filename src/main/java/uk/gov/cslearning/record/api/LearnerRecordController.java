package uk.gov.cslearning.record.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.service.UserRecordService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.unmodifiableCollection;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/records")
public class LearnerRecordController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LearnerRecordController.class);
    private UserRecordService userRecordService;

    @Autowired
    public LearnerRecordController(UserRecordService userRecordService) {
        checkArgument(userRecordService != null);
        this.userRecordService = userRecordService;
    }

    @GetMapping(path = "/{userId}")
    public ResponseEntity<Records> userRecord(@PathVariable("userId") String userId,
                                              @RequestParam(name = "activityId", required = false) List<String> activityIds) {
        LOGGER.debug("Getting user record for {}", userId);
        Collection<CourseRecord> records = userRecordService.getUserRecord(userId, activityIds);
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
