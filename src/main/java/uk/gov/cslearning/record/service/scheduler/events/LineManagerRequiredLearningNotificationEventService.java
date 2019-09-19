package uk.gov.cslearning.record.service.scheduler.events;


import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.domain.scheduler.LineManagerRequiredLearningNotificationEvent;
import uk.gov.cslearning.record.repository.LineManagerRequiredLearningNotificationEventRepository;

import java.util.List;

@Service
public class LineManagerRequiredLearningNotificationEventService {
    private LineManagerRequiredLearningNotificationEventRepository lineManagerRequiredLearningNotificationEventRepository;

    public LineManagerRequiredLearningNotificationEventService(LineManagerRequiredLearningNotificationEventRepository lineManagerRequiredLearningNotificationEventRepository) {
        this.lineManagerRequiredLearningNotificationEventRepository = lineManagerRequiredLearningNotificationEventRepository;
    }

    public LineManagerRequiredLearningNotificationEvent save(LineManagerRequiredLearningNotificationEvent lineManagerRequiredLearningNotificationEvent) {
        return lineManagerRequiredLearningNotificationEventRepository.save(lineManagerRequiredLearningNotificationEvent);
    }

    public List<LineManagerRequiredLearningNotificationEvent> findAll() {
        return lineManagerRequiredLearningNotificationEventRepository.findAll();
    }

    public void deleteAll() {
        lineManagerRequiredLearningNotificationEventRepository.deleteAll();
    }

    public void delete(LineManagerRequiredLearningNotificationEvent lineManagerRequiredLearningNotificationEvent) {
        lineManagerRequiredLearningNotificationEventRepository.delete(lineManagerRequiredLearningNotificationEvent);
    }
}