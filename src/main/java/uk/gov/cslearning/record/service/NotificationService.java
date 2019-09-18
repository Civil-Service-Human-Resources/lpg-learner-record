package uk.gov.cslearning.record.service;

import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.domain.Notification;

import java.time.LocalDateTime;

public interface NotificationService {

    @Transactional
    Notification save(Notification notification);

    @Transactional
    void deleteByLearnerUid(String uid);

    @Transactional
    void deleteAllByAge(LocalDateTime localDateTime);
}
