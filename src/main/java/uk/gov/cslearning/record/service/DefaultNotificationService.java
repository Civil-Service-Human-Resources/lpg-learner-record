package uk.gov.cslearning.record.service;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.domain.Notification;
import uk.gov.cslearning.record.domain.NotificationType;
import uk.gov.cslearning.record.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class DefaultNotificationService implements NotificationService {

    private NotificationRepository notificationRepository;

    public DefaultNotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    @Override
    public void deleteByLearnerUid(String uid) {
        notificationRepository.deleteAllByIdentityUid(uid);
    }

    @Override
    public void deleteAllByAge(LocalDateTime localDateTime) {
        notificationRepository.deleteAllBySentBefore(localDateTime);
    }

    public Optional<Notification> findByIdentityCourseAndType(String uid, String courseId, NotificationType notificationType) {
        return notificationRepository.findFirstByIdentityUidAndCourseIdAndTypeOrderBySentDesc(uid, courseId, notificationType);
    }
}
