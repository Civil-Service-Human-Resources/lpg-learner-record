package uk.gov.cslearning.record.service.scheduler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.record.domain.Notification;
import uk.gov.cslearning.record.domain.NotificationType;
import uk.gov.cslearning.record.domain.scheduler.RequiredLearningDueNotificationEvent;
import uk.gov.cslearning.record.service.DefaultNotificationService;
import uk.gov.cslearning.record.service.NotifyService;
import uk.gov.cslearning.record.service.scheduler.events.RequiredLearningDueNotificationEventService;

import java.time.Instant;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ScheduledNotificationsServiceTest {

    private final String govNotifyTemplateId = "id";

    @Mock
    private DefaultNotificationService defaultNotificationService;

    @Mock
    private NotifyService notifyService;

    @Mock
    private RequiredLearningDueNotificationEventService requiredLearningDueNotificationEventService;

    private ScheduledNotificationsService scheduledNotificationsService;

    @Before
    public void setUp() {
        scheduledNotificationsService = new ScheduledNotificationsService(defaultNotificationService, notifyService, requiredLearningDueNotificationEventService, govNotifyTemplateId);
    }

    @Test
    public void shouldReturnFalseIfRequiredLearnDueNotificationNotSent() {
        String identityUsername = "user@example.com";
        String uid = "uid";
        String courseId = "courseId";
        String courseTitle = "courseTitle";
        String period = "1 day";

        RequiredLearningDueNotificationEvent requiredLearningDueNotificationEvent = new RequiredLearningDueNotificationEvent(identityUsername, uid, courseId, courseTitle, period, Instant.now());
        when(defaultNotificationService.findByIdentityCourseAndType(uid, courseId, NotificationType.REMINDER_DAY)).thenReturn(Optional.empty());

        assertFalse(scheduledNotificationsService.hasRequiredLearningDueNotificationBeenSent(requiredLearningDueNotificationEvent));
    }

    @Test
    public void shouldReturnFalseIfRequiredLearnDueNotificationNotSentForWeek() {
        String identityUsername = "user@example.com";
        String uid = "uid";
        String courseId = "courseId";
        String courseTitle = "courseTitle";
        String period = "1 week";

        RequiredLearningDueNotificationEvent requiredLearningDueNotificationEvent = new RequiredLearningDueNotificationEvent(identityUsername, uid, courseId, courseTitle, period, Instant.now());
        when(defaultNotificationService.findByIdentityCourseAndType(uid, courseId, NotificationType.REMINDER_WEEK)).thenReturn(Optional.empty());

        assertFalse(scheduledNotificationsService.hasRequiredLearningDueNotificationBeenSent(requiredLearningDueNotificationEvent));
    }

    @Test
    public void shouldReturnFalseIfRequiredLearnDueNotificationNotSentForMonth() {
        String identityUsername = "user@example.com";
        String uid = "uid";
        String courseId = "courseId";
        String courseTitle = "courseTitle";
        String period = "1 month";

        RequiredLearningDueNotificationEvent requiredLearningDueNotificationEvent = new RequiredLearningDueNotificationEvent(identityUsername, uid, courseId, courseTitle, period, Instant.now());
        when(defaultNotificationService.findByIdentityCourseAndType(uid, courseId, NotificationType.REMINDER_MONTH)).thenReturn(Optional.empty());

        assertFalse(scheduledNotificationsService.hasRequiredLearningDueNotificationBeenSent(requiredLearningDueNotificationEvent));
    }

    @Test
    public void shouldReturnTrueIfRequiredLearningDueNotificationBeenSent() {
        String identityUsername = "user@example.com";
        String uid = "uid";
        String courseId = "courseId";
        String courseTitle = "courseTitle";
        String period = "1 day";

        RequiredLearningDueNotificationEvent requiredLearningDueNotificationEvent = new RequiredLearningDueNotificationEvent(identityUsername, uid, courseId, courseTitle, period, Instant.now());

        Notification notification = new Notification(courseId, uid, NotificationType.REMINDER_DAY);

        when(defaultNotificationService.findByIdentityCourseAndType(uid, courseId, NotificationType.REMINDER_DAY)).thenReturn(Optional.of(notification));

        assertTrue(scheduledNotificationsService.hasRequiredLearningDueNotificationBeenSent(requiredLearningDueNotificationEvent));
    }

    @Test
    public void shouldSendRequiredLearningDueNotification() {
        String username = "username@example.com";
        String uid = "uid";
        String courseId = "courseId";
        String courseTitle = "courseTitle";
        String periodText = "1 day";

        RequiredLearningDueNotificationEvent requiredLearningDueNotificationEvent = new RequiredLearningDueNotificationEvent(username, uid, courseId, courseTitle, periodText, Instant.now());
        when(notifyService.isNotifyForIncompleteCoursesSuccessful(username, courseTitle, govNotifyTemplateId, periodText)).thenReturn(true);

        scheduledNotificationsService.sendRequiredLearningDueNotification(requiredLearningDueNotificationEvent);

        verify(defaultNotificationService).save(isA(Notification.class));
    }

    @Test
    public void shouldNotSendRequiredLearningDueNotification() {
        String username = "username@example.com";
        String uid = "uid";
        String courseId = "courseId";
        String courseTitle = "courseTitle";
        String periodText = "1 day";

        RequiredLearningDueNotificationEvent requiredLearningDueNotificationEvent = new RequiredLearningDueNotificationEvent(username, uid, courseId, courseTitle, periodText, Instant.now());
        when(notifyService.isNotifyForIncompleteCoursesSuccessful(username, courseTitle, govNotifyTemplateId, periodText)).thenReturn(false);

        scheduledNotificationsService.sendRequiredLearningDueNotification(requiredLearningDueNotificationEvent);

        verify(defaultNotificationService, never()).save(any(Notification.class));
        verify(requiredLearningDueNotificationEventService, never()).delete(requiredLearningDueNotificationEvent);
    }
}
