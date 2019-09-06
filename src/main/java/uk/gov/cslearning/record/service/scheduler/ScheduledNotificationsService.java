package uk.gov.cslearning.record.service.scheduler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.domain.Notification;
import uk.gov.cslearning.record.domain.NotificationType;
import uk.gov.cslearning.record.domain.scheduler.LineManagerRequiredLearningNotificationEvent;
import uk.gov.cslearning.record.domain.scheduler.RequiredLearningDueNotificationEvent;
import uk.gov.cslearning.record.service.DefaultNotificationService;
import uk.gov.cslearning.record.service.NotifyService;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ScheduledNotificationsService {
    private DefaultNotificationService notificationService;
    private NotifyService notifyService;
    private String govNotifyCompletedLearningTemplateId;
    private String govNotifyRequiredLearningDueTemplateId;

    public ScheduledNotificationsService(DefaultNotificationService notificationService,
                                         NotifyService notifyService,
                                         @Value("${govNotify.template.completedLearning}") String govNotifyCompletedLearningTemplateId,
                                         @Value("${govNotify.template.requiredLearningDue}") String govNotifyRequiredLearningDueTemplateId) {
        this.notificationService = notificationService;
        this.notifyService = notifyService;
        this.govNotifyCompletedLearningTemplateId = govNotifyCompletedLearningTemplateId;
        this.govNotifyRequiredLearningDueTemplateId = govNotifyRequiredLearningDueTemplateId;
    }

    public Boolean hasRequiredLearningDueNotificationBeenSent(RequiredLearningDueNotificationEvent requiredLearningDueNotificationEvent) {
        NotificationType type = getType(requiredLearningDueNotificationEvent.getPeriod());
        Optional<Notification> optionalNotification = notificationService.findByIdentityCourseAndType(requiredLearningDueNotificationEvent.getIdentityUid(), requiredLearningDueNotificationEvent.getCourseId(), type);
        return optionalNotification.isPresent();
    }

    public void sendRequiredLearningDueNotification(RequiredLearningDueNotificationEvent requiredLearningDueNotificationEvent) {
        NotificationType type = getType(requiredLearningDueNotificationEvent.getPeriod());

        notifyService.notifyForIncompleteCourses(requiredLearningDueNotificationEvent.getIdentityUsername(), requiredLearningDueNotificationEvent.getCourseTitle(), govNotifyRequiredLearningDueTemplateId, requiredLearningDueNotificationEvent.getPeriod());

        Notification notification = new Notification(requiredLearningDueNotificationEvent.getCourseId(), requiredLearningDueNotificationEvent.getIdentityUid(), type);

        notificationService.save(notification);
    }

    public Boolean shouldSendLineManagerNotification(String uid, String courseId, LocalDateTime completedDate) {
        Optional<Notification> optionalNotification = notificationService.findByIdentityCourseAndType(uid, courseId, NotificationType.COMPLETE);
        return optionalNotification
                .map(notification -> notification.sentBefore(completedDate))
                .orElse(true);
    }

    public void sendLineManagerNotification(LineManagerRequiredLearningNotificationEvent lineManagerRequiredLearningNotificationEvent) {
        notifyService.notifyOnComplete(lineManagerRequiredLearningNotificationEvent.getLineManagerUsername(), govNotifyCompletedLearningTemplateId, lineManagerRequiredLearningNotificationEvent.getName(), lineManagerRequiredLearningNotificationEvent.getLineManagerUsername(), lineManagerRequiredLearningNotificationEvent.getCourseTitle());

        Notification notification = new Notification(lineManagerRequiredLearningNotificationEvent.getCourseId(), lineManagerRequiredLearningNotificationEvent.getUid(), NotificationType.COMPLETE);

        notificationService.save(notification);
    }

    public NotificationType getType(String type) {
        if ("1 day".equals(type)) {
            return NotificationType.REMINDER_DAY;
        } else if ("1 week".equals(type)) {
            return NotificationType.REMINDER_WEEK;
        } else {
            return NotificationType.REMINDER_MONTH;
        }
    }
}
