package uk.gov.cslearning.record.service.scheduler;

import org.springframework.beans.factory.annotation.Value;
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
    private String govNotifyCompletedLearningTemplateId;

    public ScheduledNotificationsService(DefaultNotificationService notificationService,
                                         NotifyService notifyService,
                                         @Value("${govNotify.template.completedLearning}") String govNotifyCompletedLearningTemplateId) {
        this.notificationService = notificationService;
        this.notifyService = notifyService;
        this.govNotifyCompletedLearningTemplateId = govNotifyCompletedLearningTemplateId;
    }

    public Boolean shoudSendNotification(String uid, String courseId, LocalDateTime completedDate) {
        Optional<Notification> optionalNotification = notificationService.findByIdentityCourseAndType(uid, courseId, NotificationType.COMPLETE);
        return optionalNotification
                .map(notification -> notification.sentBefore(completedDate))
                .orElse(true);
    }

    public void sendNotification(String lineManagerEmailAddress, String civilServantName, String civilServantUid, CourseRecord courseRecord) {
        notifyService.notifyOnComplete(lineManagerEmailAddress, govNotifyCompletedLearningTemplateId, civilServantName, lineManagerEmailAddress, courseRecord.getCourseTitle());

        Notification notification = new Notification(courseRecord.getCourseId(), civilServantUid, NotificationType.COMPLETE);
        notificationService.save(notification);
    }
}
