package uk.gov.cslearning.record.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.domain.Notification;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends CrudRepository<Notification, Long> {

    List<Notification> findAllBySentAfter(LocalDateTime sentAfter);

    @Transactional
    @Modifying
    void deleteAllByIdentityUid(String uid);

    @Transactional
    @Modifying
    void deleteAllBySentBefore(LocalDateTime localDateTime);
}
