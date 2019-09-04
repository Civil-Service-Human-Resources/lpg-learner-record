package uk.gov.cslearning.record.service.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.service.LearnerService;

@Component
public class Scheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(Scheduler.class);

    private SchedulerService schedulerService;

    private LearnerService learnerService;

    public Scheduler(SchedulerService schedulerService, LearnerService learnerService) {
        this.schedulerService = schedulerService;
        this.learnerService = learnerService;
    }

    // cron to run every day at 02:00
    @Scheduled(cron = "0 0 1 * * *")
    public void sendReminderNotificationForIncompleteLearning() throws Exception {
        LOGGER.info("Executing sendReminderNotificationForIncompleteLearning");

        schedulerService.sendReminderNotificationForIncompleteLearning();

        LOGGER.info("sendReminderNotificationForIncompleteLearning complete");
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void sendNotificationForCompletedLearning() throws Exception {
        LOGGER.info("Executing sendNotificationForCompletedLearning");

        schedulerService.sendLineManagerNotificationForCompletedLearning();

        LOGGER.info("Executing sendNotificationForCompletedLearning");
    }

    @Scheduled(cron = "0 0 4 * * *")
    public void deleteOldStatements() throws Exception {
        LOGGER.info("Executing deleteOldStatements");

        learnerService.deleteOldStatements();

        LOGGER.info("Executing sendReminderNotificationForIncompleteLearning");
    }
}
