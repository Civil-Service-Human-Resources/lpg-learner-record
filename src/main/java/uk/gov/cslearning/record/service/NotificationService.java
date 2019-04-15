package uk.gov.cslearning.record.service;

import org.springframework.transaction.annotation.Transactional;

public interface NotificationService {

    @Transactional
    void deleteByLearnerUid(String uid);
}
