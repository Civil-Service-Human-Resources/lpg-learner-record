package uk.gov.cslearning.record.service.scheduler.events;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.domain.scheduler.RequiredLearningDueNotificationEvent;
import uk.gov.cslearning.record.repository.scheduler.RequiredLearningDueNotificationEventRepository;

import java.util.List;

@Service
public class RequiredLearningDueNotificationEventService {
    private RequiredLearningDueNotificationEventRepository requiredLearningDueNotificationEventRepository;

    public RequiredLearningDueNotificationEventService(RequiredLearningDueNotificationEventRepository requiredLearningDueNotificationEventRepository) {
        this.requiredLearningDueNotificationEventRepository = requiredLearningDueNotificationEventRepository;
    }

    public RequiredLearningDueNotificationEvent save(RequiredLearningDueNotificationEvent requiredLearningDueNotificationEvent) {
        return requiredLearningDueNotificationEventRepository.save(requiredLearningDueNotificationEvent);
    }

    public List<RequiredLearningDueNotificationEvent> findAll() {
        return requiredLearningDueNotificationEventRepository.findAll();
    }

    public void delete(RequiredLearningDueNotificationEvent requiredLearningDueNotificationEvent) {
        requiredLearningDueNotificationEventRepository.delete(requiredLearningDueNotificationEvent);
    }

    public void deleteAll() {
        requiredLearningDueNotificationEventRepository.deleteAll();
    }

    public boolean doesExist(RequiredLearningDueNotificationEvent requiredLearningDueNotificationEvent) {
        return requiredLearningDueNotificationEventRepository.findFirstByIdentityUidAndCourseIdAndPeriod(requiredLearningDueNotificationEvent.getIdentityUid(), requiredLearningDueNotificationEvent.getCourseId(), requiredLearningDueNotificationEvent.getPeriod()).isPresent();
    }
}
