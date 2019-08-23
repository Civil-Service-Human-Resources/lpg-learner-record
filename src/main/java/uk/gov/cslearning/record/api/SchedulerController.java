package uk.gov.cslearning.record.api;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.cslearning.record.service.scheduler.Scheduler;

@Controller
@RequestMapping("/scheduler")
public class SchedulerController {

    private Scheduler scheduler;

    public SchedulerController(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @GetMapping(value = "/completed")
    public ResponseEntity completedLearning() throws Exception {
        scheduler.sendNotificationForCompletedLearning();
        return ResponseEntity.ok("ok");
    }

    @GetMapping(value = "/incomplete")
    public ResponseEntity incomplete() throws Exception {
        scheduler.learningJob();
        return ResponseEntity.ok("ok");
    }
}
