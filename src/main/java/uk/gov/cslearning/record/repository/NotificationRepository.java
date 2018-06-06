package uk.gov.cslearning.record.repository;

import org.springframework.data.repository.CrudRepository;
import uk.gov.cslearning.record.domain.Notification;

import java.util.Optional;

public interface NotificationRepository extends CrudRepository<Notification, Long> {

    Optional<Notification> findFirstByIdentityUidAndCourseIdOrderBySentDesc(String identityUid, String courseId);
    
    Optional<Notification> findFirstByIdentityUidAndCourseIdAndNotificationType(String identityUid, String courseId, String notificationType);

    Boolean existsByIdentityUidAndCourseId(String identityUid, String courseId);
}
