package uk.gov.cslearning.record.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.domain.Notification;
import uk.gov.cslearning.record.domain.NotificationType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class NotificationRepositoryTest {

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

    @Test
    public void notificationRepositoryShouldFindFirstByIdentityOrderedByMostRecent() {
        Long initialCount = notificationRepository.count();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        LocalDateTime dt1 = LocalDateTime.parse("2018-05-22 10:30", formatter);
        LocalDateTime dt2 = LocalDateTime.parse("2018-05-20 10:30", formatter);
        LocalDateTime dt3 = LocalDateTime.parse("2018-05-10 10:30", formatter);


        notificationRepository.save(createNotification(COURSE_ID, dt1, IDENTITY_UID, NotificationType.REMINDER)); // this is most recent and should be returned
        notificationRepository.save(createNotification(COURSE_ID, dt2, IDENTITY_UID, NotificationType.REMINDER));
        notificationRepository.save(createNotification(COURSE_ID, dt3, IDENTITY_UID, NotificationType.REMINDER));

        assertThat(notificationRepository.count(), equalTo(initialCount + 3));

        Optional<Notification> notification = notificationRepository.findFirstByIdentityUidAndCourseIdAndTypeOrderBySentDesc(IDENTITY_UID, COURSE_ID, NotificationType.REMINDER);
        assertThat(notification.get().getCourseId(), equalTo(COURSE_ID));
        assertThat(notification.get().getSent(), equalTo(dt1));
    }


    @Test
    public void notificationRepositoryShouldFindFirstByIdentityUidAndCourseIdAndNotificationType(){

        Long initialCount = notificationRepository.count();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        LocalDateTime dt1 = LocalDateTime.parse("2018-05-22 10:30", formatter);
        LocalDateTime dt2 = LocalDateTime.parse("2018-05-20 10:30", formatter);

        notificationRepository.save(createNotification(COURSE_ID, dt1, IDENTITY_UID, NotificationType.REMINDER)); // this is most recent however it is not of completed type
        notificationRepository.save(createNotification(COURSE_ID, dt2, IDENTITY_UID, NotificationType.COMPLETE)); // most recent of completed type

        assertThat(notificationRepository.count(), equalTo(initialCount + 2));

        Optional<Notification> notification = notificationRepository.findFirstByIdentityUidAndCourseIdAndTypeOrderBySentDesc(IDENTITY_UID, COURSE_ID, NotificationType.COMPLETE);
        assertThat(notification.get().getCourseId(), equalTo(COURSE_ID));
    }

    private Notification createNotification(String courseId, LocalDateTime localDateTime, String identityUid, NotificationType type) {
        Notification notification = new Notification(courseId, identityUid, type);
        notification.setSent(localDateTime);
        return notification;
    }
}