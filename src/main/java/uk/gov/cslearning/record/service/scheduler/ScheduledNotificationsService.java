package uk.gov.cslearning.record.service.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.domain.Notification;
import uk.gov.cslearning.record.domain.NotificationType;
import uk.gov.cslearning.record.domain.scheduler.RequiredLearningDueNotificationEvent;
import uk.gov.cslearning.record.service.DefaultNotificationService;
import uk.gov.cslearning.record.service.NotifyService;
import uk.gov.cslearning.record.service.scheduler.events.RequiredLearningDueNotificationEventService;

import java.util.Optional;

@Service
public class ScheduledNotificationsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledNotificationsService.class);

    private DefaultNotificationService notificationService;
    private NotifyService notifyService;
    private RequiredLearningDueNotificationEventService requiredLearningDueNotificationEventService;
    private String govNotifyRequiredLearningDueTemplateId;

    public ScheduledNotificationsService(DefaultNotificationService notificationService,
                                         NotifyService notifyService,
                                         RequiredLearningDueNotificationEventService requiredLearningDueNotificationEventService,
                                         @Value("${govNotify.template.requiredLearningDue}") String govNotifyRequiredLearningDueTemplateId) {
        this.notificationService = notificationService;
        this.notifyService = notifyService;
        this.requiredLearningDueNotificationEventService = requiredLearningDueNotificationEventService;
        this.govNotifyRequiredLearningDueTemplateId = govNotifyRequiredLearningDueTemplateId;
    }

    public Boolean hasRequiredLearningDueNotificationBeenSent(RequiredLearningDueNotificationEvent requiredLearningDueNotificationEvent) {
        NotificationType type = getType(requiredLearningDueNotificationEvent.getPeriod());
        Optional<Notification> optionalNotification = notificationService.findByIdentityCourseAndType(requiredLearningDueNotificationEvent.getIdentityUid(), requiredLearningDueNotificationEvent.getCourseId(), type);

        return optionalNotification.isPresent();
    }

    public void sendRequiredLearningDueNotification(RequiredLearningDueNotificationEvent requiredLearningDueNotificationEvent) {
        NotificationType type = getType(requiredLearningDueNotificationEvent.getPeriod());

        if (notifyService.isNotifyForIncompleteCoursesSuccessful(requiredLearningDueNotificationEvent.getIdentityUsername(), requiredLearningDueNotificationEvent.getCourseTitle(), govNotifyRequiredLearningDueTemplateId, requiredLearningDueNotificationEvent.getPeriod())) {
            Notification notification = new Notification(requiredLearningDueNotificationEvent.getCourseId(), requiredLearningDueNotificationEvent.getIdentityUid(), type);

            notificationService.save(notification);

            requiredLearningDueNotificationEventService.delete(requiredLearningDueNotificationEvent);
        } else {
            LOGGER.info("Required learning not processed {}", requiredLearningDueNotificationEvent.toString());
        }
    }

    private NotificationType getType(String type) {
        if ("1 day".equals(type)) {
            return NotificationType.REMINDER_DAY;
        } else if ("1 week".equals(type)) {
            return NotificationType.REMINDER_WEEK;
        } else {
            return NotificationType.REMINDER_MONTH;
        }
    }
}
