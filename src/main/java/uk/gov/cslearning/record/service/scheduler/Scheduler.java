package uk.gov.cslearning.record.service.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.service.LearnerService;

@Component
public class Scheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(Scheduler.class);

    private LearnerService learnerService;

    private SchedulerService schedulerService;

    public Scheduler(LearnerService learnerService, SchedulerService schedulerService) {
        this.learnerService = learnerService;
        this.schedulerService = schedulerService;
    }

    // cron to run every day at 02:00
    @Scheduled(cron = "0 0 2 * * *")
    public void sendReminderNotificationForIncompleteLearning() {
        LOGGER.info("Starting sendReminderNotificationForIncompleteLearning scheduled job");

        schedulerService.sendReminderNotificationForIncompleteLearning();

        LOGGER.info("sendReminderNotificationForIncompleteLearning complete");
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void sendNotificationForCompletedLearning() {
        LOGGER.info("Starting sendNotificationForCompletedLearning scheduled job");

        schedulerService.sendLineManagerNotificationForCompletedLearning();

        LOGGER.info("sendNotificationForCompletedLearning complete");
    }

    @Scheduled(cron = "0 0 4 * * *")
    public void deleteOldStatements() {
        LOGGER.info("Starting deleteOldStatements scheduled job");

        learnerService.deleteOldStatements();

        LOGGER.info("deleteOldStatements complete");
    }
}
