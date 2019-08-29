package uk.gov.cslearning.record.service.scheduler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.Notification;
import uk.gov.cslearning.record.domain.NotificationType;
import uk.gov.cslearning.record.service.DefaultNotificationService;
import uk.gov.cslearning.record.service.NotifyService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ScheduledNotificationsServiceTest {

    @Mock
    private DefaultNotificationService defaultNotificationService;

    @Mock
    private NotifyService notifyService;

    @InjectMocks
    private ScheduledNotificationsService scheduledNotificationsService;

    @Test
    public void shouldTrueIfNotificationNotSent() {
        String uid = "uid";
        String courseId = "courseId";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        String notificationTime = "2019-01-01 10:30";
        String completed = "2019-02-01 11:30";

        LocalDateTime completedDate = LocalDateTime.parse(completed, formatter);

        Notification notification = new Notification();
        notification.setSent(LocalDateTime.parse(notificationTime, formatter));

        when(defaultNotificationService.findByIdentityCourseAndType(uid, courseId, NotificationType.COMPLETE)).thenReturn(Optional.of(notification));
        assertTrue(scheduledNotificationsService.hasNotificationBeenSentBefore(uid, courseId, completedDate));
    }

    @Test
    public void shouldFalseIfNotificationSent() {
        String uid = "uid";
        String courseId = "courseId";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        String completed = "2019-01-01 10:30";
        String notificationTime = "2019-02-01 11:30";

        LocalDateTime completedDate = LocalDateTime.parse(completed, formatter);

        Notification notification = new Notification();
        notification.setSent(LocalDateTime.parse(notificationTime, formatter));

        when(defaultNotificationService.findByIdentityCourseAndType(uid, courseId, NotificationType.COMPLETE)).thenReturn(Optional.of(notification));
        assertFalse(scheduledNotificationsService.hasNotificationBeenSentBefore(uid, courseId, completedDate));
    }

    @Test
    public void shouldSendNotification() {
        String lineManagerEmailAddress = "lm@example.com";
        String name = "name";
        String uid = "uid";
        String courseId = "courseId";
        String courseTitle = "courseTitle";
        CourseRecord courseRecord = new CourseRecord(courseId, uid);
        courseRecord.setCourseTitle(courseTitle);

        doNothing().when(notifyService).notifyOnComplete(lineManagerEmailAddress, "", name, lineManagerEmailAddress, courseTitle);

        scheduledNotificationsService.sendNotification(lineManagerEmailAddress, name, uid, courseRecord);

        verify(defaultNotificationService).save(isA(Notification.class));
    }
}