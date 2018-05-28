package uk.gov.cslearning.record.service.scheduler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.domain.Notification;
import uk.gov.cslearning.record.domain.NotificationType;
import uk.gov.cslearning.record.service.NotifyService;
import uk.gov.cslearning.record.service.catalogue.Course;
import uk.gov.cslearning.record.service.identity.Identity;
import uk.gov.service.notify.NotificationClientException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class LearningJobTest {

    public static final String COURSE_ID = "id123";
    public static final String COURSE_TITLE_1 = "Title 1";
    public static final String COURSE_TITLE_2 = "Title 2";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public static final String EMAIL = "test@example.com";
    public static final String DAY_PERIOD = "1 day";
    public static final String WEEK_PERIOD = "1 week";
    public static final String MONTH_PERIOD = "1 month";
    public static final String IDENTITY_UID = "identity123";

    @Spy
    @InjectMocks
    private LearningJob learningJob;

    @Mock
    private NotifyService notifyService;

    @Autowired
    private ConfigurableApplicationContext c;

    private List<Course> incompleteCoursesDay;
    private List<Course> incompleteCoursesWeek;
    private List<Course> incompleteCoursesMonth;

    @Before
    public void setUp() {
        this.incompleteCoursesDay = new ArrayList<>();
        this.incompleteCoursesWeek = new ArrayList<>();
        this.incompleteCoursesMonth = new ArrayList<>();
    }

    @Test
    public void incompleteCoursesDayShouldContainCourseIfDueWithinDay() {
        Course course = new Course();
        course.setId(COURSE_ID);

        LocalDateTime nextRequiredBy = LocalDateTime.parse("2018-01-31 00:00", FORMATTER);
        LocalDateTime now = LocalDateTime.parse("2018-01-31 00:00", FORMATTER);

        learningJob.addToIncompleteCoursesIfNotificationIsNew(incompleteCoursesDay, incompleteCoursesWeek, incompleteCoursesMonth, now, course, nextRequiredBy);
        assertThat(incompleteCoursesWeek.size(), equalTo(0));
        assertThat(incompleteCoursesMonth.size(), equalTo(0));

        assertThat(incompleteCoursesDay.size(), equalTo(1));
        assertThat(incompleteCoursesDay.get(0).getId(), equalTo(COURSE_ID));

    }

    @Test
    public void incompleteCoursesWeekShouldContainCourseIfDueWithinWeek() {
        Course course = new Course();
        course.setId(COURSE_ID);

        LocalDateTime nextRequiredBy = LocalDateTime.parse("2018-01-31 00:00", FORMATTER);
        LocalDateTime now = LocalDateTime.parse("2018-01-25 00:00", FORMATTER);

        learningJob.addToIncompleteCoursesIfNotificationIsNew(incompleteCoursesDay, incompleteCoursesWeek, incompleteCoursesMonth, now, course, nextRequiredBy);
        assertThat(incompleteCoursesDay.size(), equalTo(0));
        assertThat(incompleteCoursesMonth.size(), equalTo(0));

        assertThat(incompleteCoursesWeek.size(), equalTo(1));
        assertThat(incompleteCoursesWeek.get(0).getId(), equalTo(COURSE_ID));

    }

    @Test
    public void incompleteCoursesMonthShouldContainCourseIfDueWithinMonth() {
        Course course = new Course();
        course.setId(COURSE_ID);

        LocalDateTime nextRequiredBy = LocalDateTime.parse("2018-01-31 00:00", FORMATTER);
        LocalDateTime now = LocalDateTime.parse("2018-01-08 00:00", FORMATTER);

        learningJob.addToIncompleteCoursesIfNotificationIsNew(incompleteCoursesDay, incompleteCoursesWeek, incompleteCoursesMonth, now, course, nextRequiredBy);
        assertThat(incompleteCoursesDay.size(), equalTo(0));
        assertThat(incompleteCoursesWeek.size(), equalTo(0));

        assertThat(incompleteCoursesMonth.size(), equalTo(1));
        assertThat(incompleteCoursesMonth.get(0).getId(), equalTo(COURSE_ID));
    }

    @Test
    public void incompleteCoursesShouldBeEmptyIfNotDue() {
        Course course = new Course();
        course.setId(COURSE_ID);

        LocalDateTime nextRequiredBy = LocalDateTime.parse("2018-02-20 00:00", FORMATTER);
        LocalDateTime now = LocalDateTime.parse("2018-01-01 00:00", FORMATTER);

        learningJob.addToIncompleteCoursesIfNotificationIsNew(incompleteCoursesDay, incompleteCoursesWeek, incompleteCoursesMonth, now, course, nextRequiredBy);
        assertThat(incompleteCoursesDay.size(), equalTo(0));
        assertThat(incompleteCoursesWeek.size(), equalTo(0));
        assertThat(incompleteCoursesMonth.size(), equalTo(0));
    }

    @Test
    public void testArgumentsOfLearningNotifications() throws NotificationClientException {
        Identity identity = new Identity();
        identity.setUsername(EMAIL);

        Course course1 = new Course();
        course1.setTitle(COURSE_TITLE_1);
        Course course2 = new Course();
        course2.setTitle(COURSE_TITLE_2);
        incompleteCoursesMonth.add(course1);
        incompleteCoursesMonth.add(course2);

        learningJob.sendNotifiyForPeriod(identity, incompleteCoursesMonth, MONTH_PERIOD);

        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> requiredLearningCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> templateIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> periodCaptor = ArgumentCaptor.forClass(String.class);

        verify(notifyService).notify(emailCaptor.capture(), requiredLearningCaptor.capture(), templateIdCaptor.capture(), periodCaptor.capture());
        assertThat(emailCaptor.getValue(), equalTo(EMAIL));

        String expectedRequiredLearning = COURSE_TITLE_1 + "\n" + COURSE_TITLE_2 + "\n";
        assertThat(requiredLearningCaptor.getValue(), equalTo(expectedRequiredLearning));

        assertThat(periodCaptor.getValue(), equalTo(MONTH_PERIOD));
    }

    @Test
    public void testSendNotifyForIncompleteCoursesForDay() throws NotificationClientException {
        Identity identity = new Identity();
        identity.setUsername(EMAIL);

        Course course = new Course();
        course.setTitle(COURSE_TITLE_1);
        incompleteCoursesDay.add(course);

        learningJob.sendNotifyForIncompleteCourses(identity, incompleteCoursesDay, incompleteCoursesWeek, incompleteCoursesMonth);
        ArgumentCaptor<String> periodCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Identity> identityCaptor = ArgumentCaptor.forClass(Identity.class);

        ArgumentCaptor<List<Course>> incompleteCoursesDayCaptor = ArgumentCaptor.forClass(List.class);

        verify(learningJob).sendNotifiyForPeriod(identityCaptor.capture(), incompleteCoursesDayCaptor.capture(), periodCaptor.capture());

        List<Course> expectedIncompleteCoursesDay = incompleteCoursesDayCaptor.getValue();
        assertThat(expectedIncompleteCoursesDay.size(), equalTo(1));
        assertThat(expectedIncompleteCoursesDay.get(0).getTitle(), equalTo(COURSE_TITLE_1));

        assertThat(periodCaptor.getValue(), equalTo(DAY_PERIOD));

        assertThat(incompleteCoursesDay.size(), equalTo(1));
        assertThat(incompleteCoursesWeek.size(), equalTo(0));
        assertThat(incompleteCoursesMonth.size(), equalTo(0));

    }

    @Test
    public void courseShouldBeAddedToWeekListIfMonthNotificationSentOver23Days() {
        Course course = new Course();
        course.setTitle(COURSE_TITLE_1);

        LocalDateTime sent = LocalDateTime.parse("2018-05-20 00:00", FORMATTER);
        Notification notification = new Notification(COURSE_ID, sent, NotificationType.MONTH, IDENTITY_UID);

        LocalDateTime now = LocalDateTime.parse("2018-05-22 00:00", FORMATTER);

        learningJob.addIncompleteCoursesToList(incompleteCoursesDay, incompleteCoursesWeek, incompleteCoursesMonth, course, notification, now);

        assertThat(incompleteCoursesDay.size(), equalTo(0));
        assertThat(incompleteCoursesWeek.size(), equalTo(1));
        assertThat(incompleteCoursesMonth.size(), equalTo(0));
    }

    @Test
    public void courseShouldBeAddedToDayListIfMonthNotificationSentOver23Days() {
        Course course = new Course();
        course.setTitle(COURSE_TITLE_1);

        LocalDateTime sent = LocalDateTime.parse("2018-05-29 05:00", FORMATTER);
        Notification notification = new Notification(COURSE_ID, sent, NotificationType.MONTH, IDENTITY_UID);

        LocalDateTime now = LocalDateTime.parse("2018-05-30 00:00", FORMATTER);

        learningJob.addIncompleteCoursesToList(incompleteCoursesDay, incompleteCoursesWeek, incompleteCoursesMonth, course, notification, now);

        assertThat(incompleteCoursesDay.size(), equalTo(1));
        assertThat(incompleteCoursesWeek.size(), equalTo(0));
        assertThat(incompleteCoursesMonth.size(), equalTo(0));
    }

    @Test
    public void courseShouldBeAddedIfNotificationOld() {
        Course course = new Course();
        course.setTitle(COURSE_TITLE_1);

        LocalDateTime sent = LocalDateTime.parse("2017-05-20 00:00", FORMATTER);
        Notification notification = new Notification(COURSE_ID, sent, NotificationType.MONTH, IDENTITY_UID);

        LocalDateTime now = LocalDateTime.parse("2018-05-22 00:00", FORMATTER);

        learningJob.addIncompleteCoursesToList(incompleteCoursesDay, incompleteCoursesWeek, incompleteCoursesMonth, course, notification, now);

        assertThat(incompleteCoursesDay.size(), equalTo(0));
        assertThat(incompleteCoursesWeek.size(), equalTo(0));
        assertThat(incompleteCoursesMonth.size(), equalTo(1));
    }

    @Test
    public void shouldBeAddedToDayListIfDayNotificationNotSent(){
        Course course = new Course();
        course.setTitle(COURSE_TITLE_1);

        LocalDateTime sent = LocalDateTime.parse("2018-05-19 00:00", FORMATTER);
        Notification notification = new Notification(COURSE_ID, sent, NotificationType.DAY, IDENTITY_UID);

        LocalDateTime now = LocalDateTime.parse("2018-05-22 00:00", FORMATTER);

        learningJob.addIncompleteCoursesToList(incompleteCoursesDay, incompleteCoursesWeek, incompleteCoursesMonth, course, notification, now);

        assertThat(incompleteCoursesDay.size(), equalTo(1));
        assertThat(incompleteCoursesWeek.size(), equalTo(0));
        assertThat(incompleteCoursesMonth.size(), equalTo(0));
    }

    @Test
    public void shouldNotBeAddedToDayListIfDayNotificationRecentlySent(){
        Course course = new Course();
        course.setTitle(COURSE_TITLE_1);

        LocalDateTime sent = LocalDateTime.parse("2018-05-22 00:00", FORMATTER);
        Notification notification = new Notification(COURSE_ID, sent, NotificationType.DAY, IDENTITY_UID);

        LocalDateTime now = LocalDateTime.parse("2018-05-22 03:00", FORMATTER);

        learningJob.addIncompleteCoursesToList(incompleteCoursesDay, incompleteCoursesWeek, incompleteCoursesMonth, course, notification, now);

        assertThat(incompleteCoursesDay.size(), equalTo(0));
        assertThat(incompleteCoursesWeek.size(), equalTo(0));
        assertThat(incompleteCoursesMonth.size(), equalTo(0));
    }

    @Test
    public void shouldBeAddedToWeekListIfDueInLessThanAWeek(){
        Course course = new Course();
        course.setTitle(COURSE_TITLE_1);

        LocalDateTime sent = LocalDateTime.parse("2018-05-10 00:00", FORMATTER);
        Notification notification = new Notification(COURSE_ID, sent, NotificationType.WEEK, IDENTITY_UID);

        LocalDateTime now = LocalDateTime.parse("2018-05-22 00:00", FORMATTER);

        learningJob.addIncompleteCoursesToList(incompleteCoursesDay, incompleteCoursesWeek, incompleteCoursesMonth, course, notification, now);

        assertThat(incompleteCoursesDay.size(), equalTo(0));
        assertThat(incompleteCoursesWeek.size(), equalTo(1));
        assertThat(incompleteCoursesMonth.size(), equalTo(0));
    }

}