package uk.gov.cslearning.record.repository.scheduler;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.record.domain.scheduler.RequiredLearningDueNotificationEvent;

import java.util.Optional;

@Repository
public interface RequiredLearningDueNotificationEventRepository extends JpaRepository<RequiredLearningDueNotificationEvent, Long> {
    Optional<RequiredLearningDueNotificationEvent> findFirstByIdentityUidAndCourseIdAndPeriod(String identityUid, String courseId, String period);
}
