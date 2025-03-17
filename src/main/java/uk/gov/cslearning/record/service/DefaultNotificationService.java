package uk.gov.cslearning.record.service;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.repository.NotificationRepository;

import java.time.LocalDateTime;

@Service
public class DefaultNotificationService implements NotificationService {

    private final NotificationRepository notificationRepository;

    public DefaultNotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void deleteByLearnerUid(String uid) {
        notificationRepository.deleteAllByIdentityUid(uid);
    }

    @Override
    public void deleteAllByAge(LocalDateTime localDateTime) {
        notificationRepository.deleteAllBySentBefore(localDateTime);
    }
}
