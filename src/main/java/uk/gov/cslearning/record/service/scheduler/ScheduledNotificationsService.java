package uk.gov.cslearning.record.service.scheduler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.Notification;
import uk.gov.cslearning.record.domain.NotificationType;
import uk.gov.cslearning.record.dto.IdentityDTO;
import uk.gov.cslearning.record.service.DefaultNotificationService;
import uk.gov.cslearning.record.service.NotifyService;
import uk.gov.cslearning.record.service.catalogue.Course;

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

    public Boolean shouldSendLineManagerNotification(String uid, String courseId, LocalDateTime completedDate) {
        Optional<Notification> optionalNotification = notificationService.findByIdentityCourseAndType(uid, courseId, NotificationType.COMPLETE);
        return optionalNotification
                .map(notification -> notification.sentBefore(completedDate))
                .orElse(true);
    }

    public Boolean hasRequiredLearningDueNotificationBeenSent(String uid, String courseId, LocalDateTime completedDate) {
        Optional<Notification> optionalNotification = notificationService.findByIdentityCourseAndType(uid, courseId, NotificationType.REMINDER);
        return optionalNotification.isPresent();
    }

    public void sendLineManagerNotification(String lineManagerEmailAddress, String civilServantName, String civilServantUid, CourseRecord courseRecord) {
        notifyService.notifyOnComplete(lineManagerEmailAddress, govNotifyCompletedLearningTemplateId, civilServantName, lineManagerEmailAddress, courseRecord.getCourseTitle());

        Notification notification = new Notification(courseRecord.getCourseId(), civilServantUid, NotificationType.COMPLETE);
        notificationService.save(notification);
    }

    public void sendRequiredLearningDueNotification(IdentityDTO identity, Course course, String periodText) {
        notifyService.notifyForIncompleteCourses(identity.getUsername(), course.getTitle(), govNotifyRequiredLearningDueTemplateId, periodText);

        Notification notification = new Notification(course.getId(), identity.getUid(), NotificationType.REMINDER);
        notificationService.save(notification);
    }
}
