package uk.gov.cslearning.record.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cslearning.record.service.scheduler.Scheduler;

@Slf4j
@RestController("/jobs")
public class ManualJobController {

    private Scheduler scheduler;

    @Autowired
    public ManualJobController(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @GetMapping
    public void startJob(@RequestParam(name = "job") String job) {
        switch (job) {
            case "refresh":
                log.info("Manual request for learner record refresh job");
                scheduler.courseDataRefresh();
                log.info("Learner record refresh started");
                break;
            case "incomplete":
                log.info("Manual request for incomplete learning job");
                scheduler.learningJob();
                log.info("Incomplete learning job started");
                break;
            case "complete":
                log.info("Manual request for complete learning job");
                scheduler.sendNotificationForCompletedLearning();
                log.info("Complete learning job started");
                break;
            default:
                log.info("Manual request for unknown job, nothing done");
                break;
        }
    }
}
