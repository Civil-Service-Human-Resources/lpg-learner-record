package uk.gov.cslearning.record.repository;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.IntegrationTestBase;
import uk.gov.cslearning.record.domain.Notification;
import uk.gov.cslearning.record.domain.NotificationType;

import java.time.LocalDateTime;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@Transactional
public class NotificationRepositoryTest extends IntegrationTestBase {

    private static final String COURSE_ID = "course123";

    private static final String IDENTITY_UID = "123abc";

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    public void notificationRepositoryShouldSave() {
        Long initialCount = notificationRepository.count();

        notificationRepository.save(createNotification(COURSE_ID, LocalDateTime.now(), IDENTITY_UID, NotificationType.REMINDER));

        assertThat(notificationRepository.count(), equalTo(initialCount + 1));
    }

    private Notification createNotification(String courseId, LocalDateTime localDateTime, String identityUid, NotificationType type) {
        Notification notification = new Notification(courseId, identityUid, type);
        notification.setSent(localDateTime);
        return notification;
    }
}
