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

    private SchedulerEventNotificationService schedulerEventNotificationService;

    public Scheduler(LearnerService learnerService, SchedulerService schedulerService, SchedulerEventNotificationService schedulerEventNotificationService) {
        this.learnerService = learnerService;
        this.schedulerService = schedulerService;
        this.schedulerEventNotificationService = schedulerEventNotificationService;
    }

    // cron to run every day at 01:00
    @Scheduled(cron = "0 0 1 * * *")
    public void processReminderNotificationForIncompleteLearning() {
        LOGGER.info("Starting processReminderNotificationForIncompleteLearning scheduled job");

        schedulerService.processReminderNotificationForIncompleteLearning();

        LOGGER.info("processReminderNotificationForIncompleteLearning complete");
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void processLineManagerNotificationForCompletedLearning() {
        LOGGER.info("Starting processLineManagerNotificationForCompletedLearning scheduled job");

        schedulerService.processLineManagerNotificationForCompletedLearning();

        LOGGER.info("processLineManagerNotificationForCompletedLearning complete");
    }

    @Scheduled(cron = "0 0 4 * * *")
    public void deleteOldStatements() {
        LOGGER.info("Starting deleteOldStatements scheduled job");

        learnerService.deleteOldStatements();

        LOGGER.info("deleteOldStatements complete");
    }

    @Scheduled(cron = "0 0 5 * * *")
    public void sendReminderNotificationForIncompleteLearning() {
        LOGGER.info("Starting sendReminderNotificationForIncompleteLearning scheduled job");

        schedulerEventNotificationService.sendReminderNotificationForIncompleteLearning();

        LOGGER.info("sendReminderNotificationForIncompleteLearning complete");
    }

    @Scheduled(cron = "0 0 6 * * *")
    public void sendLineManagerCompleteNotifications() {
        LOGGER.info("Starting sendLineManagerCompleteNotifications scheduled job");

        schedulerEventNotificationService.sendLineManagerCompleteNotifications();

        LOGGER.info("sendLineManagerCompleteNotifications complete");
    }
}
