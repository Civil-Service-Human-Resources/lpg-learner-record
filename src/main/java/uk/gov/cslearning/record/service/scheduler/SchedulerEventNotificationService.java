package uk.gov.cslearning.record.service.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.domain.scheduler.LineManagerRequiredLearningNotificationEvent;
import uk.gov.cslearning.record.domain.scheduler.RequiredLearningDueNotificationEvent;
import uk.gov.cslearning.record.service.scheduler.events.LineManagerRequiredLearningNotificationEventService;
import uk.gov.cslearning.record.service.scheduler.events.RequiredLearningDueNotificationEventService;

import java.util.List;

@Service
public class SchedulerEventNotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerEventNotificationService.class);

    private RequiredLearningDueNotificationEventService requiredLearningDueNotificationEventService;

    private LineManagerRequiredLearningNotificationEventService lineManagerRequiredLearningNotificationEventService;

    private ScheduledNotificationsService scheduledNotificationsService;

    public SchedulerEventNotificationService(RequiredLearningDueNotificationEventService requiredLearningDueNotificationEventService, LineManagerRequiredLearningNotificationEventService lineManagerRequiredLearningNotificationEventService, ScheduledNotificationsService scheduledNotificationsService) {
        this.requiredLearningDueNotificationEventService = requiredLearningDueNotificationEventService;
        this.lineManagerRequiredLearningNotificationEventService = lineManagerRequiredLearningNotificationEventService;
        this.scheduledNotificationsService = scheduledNotificationsService;
    }

    @Transactional
    public void sendReminderNotificationForIncompleteLearning() {
        LOGGER.info("sendLineManagerNotificationForCompletedLearning");

        List<RequiredLearningDueNotificationEvent> requiredLearningDueNotificationEventList = requiredLearningDueNotificationEventService.findAll();

        requiredLearningDueNotificationEventList.forEach(requiredLearningEventDue -> {
            if (scheduledNotificationsService.hasRequiredLearningDueNotificationBeenSent(requiredLearningEventDue)) {
                LOGGER.info("Required learning due notification has already been sent");
            } else {
                scheduledNotificationsService.sendRequiredLearningDueNotification(requiredLearningEventDue);
            }
        });

        requiredLearningDueNotificationEventService.deleteAll();

        LOGGER.info("Complete");
    }

    @Transactional
    public void sendLineManagerCompleteNotifications() {
        LOGGER.info("sendLineManagerNotificationForCompletedLearning");

        List<LineManagerRequiredLearningNotificationEvent> lineManagerRequiredLearningNotificationEventList = lineManagerRequiredLearningNotificationEventService.findAll();

        lineManagerRequiredLearningNotificationEventList.forEach(lineManagerRequiredLearningNotificationEvent -> {
            if (scheduledNotificationsService.hasRequiredLearningDueNotificationBeenSent(lineManagerRequiredLearningNotificationEvent.getUid(), lineManagerRequiredLearningNotificationEvent.getCourseId())) {
                LOGGER.info("Line manager complete notification has already been sent");
            } else {
                scheduledNotificationsService.sendLineManagerNotification(lineManagerRequiredLearningNotificationEvent);
            }
        });

        lineManagerRequiredLearningNotificationEventService.deleteAll();

        LOGGER.info("Complete");
    }
}
