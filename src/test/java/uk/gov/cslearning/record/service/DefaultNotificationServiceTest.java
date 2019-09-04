package uk.gov.cslearning.record.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.record.domain.Notification;
import uk.gov.cslearning.record.domain.NotificationType;
import uk.gov.cslearning.record.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.time.Month;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DefaultNotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private DefaultNotificationService defaultNotificationService;

    @Test
    public void shouldSave() {
        Notification notification = new Notification();

        defaultNotificationService.save(notification);

        verify(notificationRepository).save(notification);
    }

    @Test
    public void shouldDeleteByLearnerUid() {
        String uid = "uid";

        defaultNotificationService.deleteByLearnerUid(uid);

        verify(notificationRepository).deleteAllByIdentityUid(uid);
    }

    @Test
    public void shouldDeleteAllByAge() {
        LocalDateTime dateTime = LocalDateTime.of(2019, Month.FEBRUARY, 21, 9, 0);

        defaultNotificationService.deleteAllByAge(dateTime);

        verify(notificationRepository).deleteAllBySentBefore(dateTime);
    }

    @Test
    public void shouldFindByIdentityCourseAndType() {
        String uid = "uid";
        String courseId = "courseId";
        NotificationType notificationType = NotificationType.COMPLETE;

        defaultNotificationService.findByIdentityCourseAndType(uid, courseId, notificationType);

        verify(notificationRepository).findFirstByIdentityUidAndCourseIdAndTypeOrderBySentDesc(uid, courseId, notificationType);
    }
}
