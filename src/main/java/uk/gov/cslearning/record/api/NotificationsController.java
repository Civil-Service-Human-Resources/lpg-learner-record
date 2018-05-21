package uk.gov.cslearning.record.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.service.ActivityRecordService;
import uk.gov.cslearning.record.service.UserRecordService;
import uk.gov.cslearning.record.service.scheduler.Scheduler;

import java.util.ArrayList;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.unmodifiableCollection;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/notifications")
public class NotificationsController {

    private Scheduler scheduler;

    @Autowired
    public NotificationsController(Scheduler scheduler) {
        checkArgument(scheduler != null);
        this.scheduler = scheduler;
    }

    @GetMapping
    public ResponseEntity<Void> triggerJob() throws Exception {
        scheduler.learningJob();
        return ResponseEntity.ok().build();
    }
}
