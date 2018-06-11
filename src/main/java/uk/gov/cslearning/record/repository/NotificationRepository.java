package uk.gov.cslearning.record.repository;

import org.springframework.data.repository.CrudRepository;
import uk.gov.cslearning.record.domain.Notification;
import uk.gov.cslearning.record.domain.NotificationType;

import java.util.Optional;

public interface NotificationRepository extends CrudRepository<Notification, Long> {

    Optional<Notification> findFirstByIdentityUidAndCourseIdAndTypeOrderBySentDesc(String identityUid, String courseId, NotificationType type);
}
