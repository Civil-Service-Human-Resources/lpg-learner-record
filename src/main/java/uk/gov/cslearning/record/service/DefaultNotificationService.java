package uk.gov.cslearning.record.service;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.repository.NotificationRepository;

@Service
public class DefaultNotificationService implements NotificationService {

    private NotificationRepository notificationRepository;

    public DefaultNotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void deleteByLearnerUid(String uid) {
        notificationRepository.deleteAllByIdentityUid(uid);
    }
}
