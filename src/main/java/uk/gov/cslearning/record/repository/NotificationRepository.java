package uk.gov.cslearning.record.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.domain.Notification;
import uk.gov.cslearning.record.domain.NotificationType;

import java.time.LocalDateTime;
import java.util.Optional;

public interface NotificationRepository extends CrudRepository<Notification, Long> {

    Optional<Notification> findFirstByIdentityUidAndCourseIdAndTypeOrderBySentDesc(String identityUid, String courseId, NotificationType type);

    @Transactional
    @Modifying
    void deleteAllByIdentityUid(String uid);

    @Transactional
    @Modifying
    void deleteAllBySentBefore(LocalDateTime localDateTime);
}
