package uk.gov.cslearning.record.service;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

public interface NotificationService {

    @Transactional
    void deleteByLearnerUid(String uid);

    @Transactional
    void deleteAllByAge(LocalDateTime localDateTime);
}
