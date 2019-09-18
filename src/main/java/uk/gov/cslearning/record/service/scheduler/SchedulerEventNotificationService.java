package uk.gov.cslearning.record.service.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.domain.scheduler.RequiredLearningDueNotificationEvent;
import uk.gov.cslearning.record.service.scheduler.events.RequiredLearningDueNotificationEventService;

import java.util.List;

@Service
public class SchedulerEventNotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerEventNotificationService.class);

    private RequiredLearningDueNotificationEventService requiredLearningDueNotificationEventService;

    private ScheduledNotificationsService scheduledNotificationsService;

    public SchedulerEventNotificationService(RequiredLearningDueNotificationEventService requiredLearningDueNotificationEventService, ScheduledNotificationsService scheduledNotificationsService) {
        this.requiredLearningDueNotificationEventService = requiredLearningDueNotificationEventService;
        this.scheduledNotificationsService = scheduledNotificationsService;
    }

    @Transactional
    public void sendReminderNotificationForIncompleteLearning() {
        List<RequiredLearningDueNotificationEvent> requiredLearningDueNotificationEventList = requiredLearningDueNotificationEventService.findAll();

        requiredLearningDueNotificationEventList.forEach(requiredLearningEventDue -> {
            if (scheduledNotificationsService.hasRequiredLearningDueNotificationBeenSent(requiredLearningEventDue)) {
                LOGGER.info("Required learning due notification has already been sent");
            } else {
                scheduledNotificationsService.sendRequiredLearningDueNotification(requiredLearningEventDue);
            }
        });
    }
}
