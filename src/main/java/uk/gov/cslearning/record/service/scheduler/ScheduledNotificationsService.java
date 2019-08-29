package uk.gov.cslearning.record.service.scheduler;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.Notification;
import uk.gov.cslearning.record.domain.NotificationType;
import uk.gov.cslearning.record.service.DefaultNotificationService;
import uk.gov.cslearning.record.service.NotifyService;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ScheduledNotificationsService {

    private DefaultNotificationService notificationService;
    private NotifyService notifyService;
    private String govNotifyCompletedLearningTemplateId = "";

    public ScheduledNotificationsService(DefaultNotificationService notificationService, NotifyService notifyService) {
        this.notificationService = notificationService;
        this.notifyService = notifyService;
    }

    public Boolean hasNotificationBeenSentBefore(String uid, String courseId, LocalDateTime completedDate) {
        Optional<Notification> optionalNotification = notificationService.findByIdentityCourseAndType(uid, courseId, NotificationType.COMPLETE);
        return optionalNotification
                .map(notification -> notification.sentBefore(completedDate))
                .orElse(false);
    }

    public void sendNotification(String lineManagerEmailAddress, String civilServantName, String civilServantUid, CourseRecord courseRecord) {
        notifyService.notifyOnComplete(lineManagerEmailAddress, govNotifyCompletedLearningTemplateId, civilServantName, lineManagerEmailAddress, courseRecord.getCourseTitle());

        Notification notification = new Notification(courseRecord.getCourseId(), civilServantUid, NotificationType.COMPLETE);
        notificationService.save(notification);
    }
}
