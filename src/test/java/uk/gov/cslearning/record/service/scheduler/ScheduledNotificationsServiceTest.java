package uk.gov.cslearning.record.service.scheduler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.Notification;
import uk.gov.cslearning.record.domain.NotificationType;
import uk.gov.cslearning.record.domain.scheduler.LineManagerRequiredLearningNotificationEvent;
import uk.gov.cslearning.record.domain.scheduler.RequiredLearningDueNotificationEvent;
import uk.gov.cslearning.record.service.DefaultNotificationService;
import uk.gov.cslearning.record.service.NotifyService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ScheduledNotificationsServiceTest {

    private final String govNotifyTemplateId = "id";

    @Mock
    private DefaultNotificationService defaultNotificationService;

    @Mock
    private NotifyService notifyService;

    private ScheduledNotificationsService scheduledNotificationsService;

    @Before
    public void setUp() {
        scheduledNotificationsService = new ScheduledNotificationsService(defaultNotificationService, notifyService, govNotifyTemplateId, govNotifyTemplateId);
    }

    @Test
    public void shouldReturnTrueIfNotificationNotSent() {
        String uid = "uid";
        String courseId = "courseId";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        String notificationTime = "2019-01-01 10:30";
        String completed = "2019-02-01 11:30";

        LocalDateTime completedDate = LocalDateTime.parse(completed, formatter);

        Notification notification = new Notification();
        notification.setSent(LocalDateTime.parse(notificationTime, formatter));

        when(defaultNotificationService.findByIdentityCourseAndType(uid, courseId, NotificationType.COMPLETE)).thenReturn(Optional.of(notification));

        assertTrue(scheduledNotificationsService.hasRequiredLearningDueNotificationBeenSent(uid, courseId));
    }

    @Test
    public void shouldSendLineManagerNotification() {
        String lineManagerEmailAddress = "lm@example.com";
        String name = "name";
        String uid = "uid";
        String courseId = "courseId";
        String courseTitle = "courseTitle";
        CourseRecord courseRecord = new CourseRecord(courseId, uid);
        courseRecord.setCourseTitle(courseTitle);

        doNothing().when(notifyService).notifyOnComplete(lineManagerEmailAddress, govNotifyTemplateId, name, lineManagerEmailAddress, courseTitle);

        LineManagerRequiredLearningNotificationEvent lineManagerRequiredLearningNotificationEvent = new LineManagerRequiredLearningNotificationEvent(lineManagerEmailAddress, name, uid, courseId, courseTitle, Instant.now());
        scheduledNotificationsService.sendLineManagerNotification(lineManagerRequiredLearningNotificationEvent);

        verify(defaultNotificationService).save(isA(Notification.class));
    }

    @Test
    public void shouldSendRequiredLearningDueNotification() {
        String username = "username@example.com";
        String uid = "uid";
        String courseId = "courseId";
        String courseTitle = "courseTitle";
        String periodText = "1 day";

        RequiredLearningDueNotificationEvent requiredLearningDueNotificationEvent = new RequiredLearningDueNotificationEvent(username, uid, courseId, courseTitle, periodText, Instant.now());
        doNothing().when(notifyService).notifyForIncompleteCourses(username, courseTitle, govNotifyTemplateId, periodText);

        scheduledNotificationsService.sendRequiredLearningDueNotification(requiredLearningDueNotificationEvent);

        verify(defaultNotificationService).save(isA(Notification.class));
    }
}