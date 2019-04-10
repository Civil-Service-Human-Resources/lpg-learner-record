package uk.gov.cslearning.record.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface LearnerService {

    @Transactional
    void deleteLearnerByUid(String uid);
}
