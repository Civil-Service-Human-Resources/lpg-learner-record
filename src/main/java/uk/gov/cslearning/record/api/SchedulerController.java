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

    @GetMapping("process-reminders")
    public ResponseEntity processReminderNotificationForIncompleteLearning() {
        scheduler.processReminderNotificationForIncompleteLearning();
        return ResponseEntity.ok("OK");
    }

    @GetMapping("send-reminders")
    public ResponseEntity<String> sendReminderNotificationForIncompleteLearning() {
        scheduler.sendReminderNotificationForIncompleteLearning();
        return ResponseEntity.ok("OK");
    }

    @GetMapping("process-completed")
    public ResponseEntity<String> processLineManagerNotificationForCompletedLearning() {
        scheduler.processLineManagerNotificationForCompletedLearning();
        return ResponseEntity.ok("OK");
    }

    @GetMapping("send-completed")
    public ResponseEntity<String> sendLineManagerCompleteNotifications() {
        scheduler.sendLineManagerCompleteNotifications();
        return ResponseEntity.ok("OK");
    }
}
