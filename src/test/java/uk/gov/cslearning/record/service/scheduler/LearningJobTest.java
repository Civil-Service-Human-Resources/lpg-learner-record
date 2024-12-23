package uk.gov.cslearning.record.service.scheduler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.csrs.service.RegistryService;
import uk.gov.cslearning.record.domain.Notification;
import uk.gov.cslearning.record.domain.NotificationType;
import uk.gov.cslearning.record.repository.CourseRecordRepository;
import uk.gov.cslearning.record.repository.NotificationRepository;
import uk.gov.cslearning.record.service.NotifyService;
import uk.gov.cslearning.record.service.catalogue.Course;
import uk.gov.cslearning.record.service.identity.Identity;
import uk.gov.cslearning.record.service.identity.IdentityService;
import uk.gov.service.notify.NotificationClientException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class LearningJobTest {

    private static final String COURSE_ID = "id123";
    private static final String COURSE_TITLE_1 = "Title 1";
    private static final String COURSE_TITLE_2 = "Title 2";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String EMAIL = "test@example.com";
    private static final String MONTH_PERIOD = "1 month";
    private static final String IDENTITY_UID = "identity123";

    @Spy
    @InjectMocks
    private LearningJob learningJob;

    @Mock
    private NotifyService notifyService;

    @Mock
    private IdentityService identityService;

    @Mock
    private RegistryService registryService;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private CourseRecordRepository courseRecordRepository;

    private List<Course> incompleteCoursesDay;
    private List<Course> incompleteCoursesWeek;
    private List<Course> incompleteCoursesMonth;

    private Map<Long, List<Course>> incompleteCourses;

    private Identity identity;

    @Before
    public void setUp() {
        incompleteCoursesDay = new ArrayList<>();
        incompleteCoursesWeek = new ArrayList<>();
        incompleteCoursesMonth = new ArrayList<>();
        incompleteCourses = new HashMap<>();

        incompleteCourses.put(1L, incompleteCoursesDay);
        incompleteCourses.put(7L, incompleteCoursesWeek);
        incompleteCourses.put(30L, incompleteCoursesMonth);

        identity = new Identity();
        identity.setUid("uid");

        when(notificationRepository.findFirstByIdentityUidAndCourseIdAndTypeOrderBySentDesc(anyString(), anyString(), any()))
                .thenReturn(Optional.empty());
    }

    @Test
    public void incompleteCoursesDayShouldContainCourseIfDueWithinDay() {
        Course course = new Course();
        course.setId(COURSE_ID);

        LocalDate nextRequiredBy = LocalDate.parse("2018-01-31", FORMATTER);
        LocalDate now = LocalDate.parse("2018-01-31", FORMATTER);

        learningJob.checkAndAdd(course, identity, nextRequiredBy, now, incompleteCourses);

        assertThat(incompleteCoursesWeek.size(), equalTo(0));
        assertThat(incompleteCoursesMonth.size(), equalTo(0));

        assertThat(incompleteCoursesDay.size(), equalTo(1));
        assertThat(incompleteCoursesDay.get(0).getId(), equalTo(COURSE_ID));
    }

    @Test
    public void incompleteCoursesWeekShouldContainCourseIfDueWithinWeek() {
        Course course = new Course();
        course.setId(COURSE_ID);

        LocalDate nextRequiredBy = LocalDate.parse("2018-01-31", FORMATTER);
        LocalDate now = LocalDate.parse("2018-01-25", FORMATTER);

        learningJob.checkAndAdd(course, identity, nextRequiredBy, now, incompleteCourses);

        assertThat(incompleteCoursesDay.size(), equalTo(0));
        assertThat(incompleteCoursesMonth.size(), equalTo(0));

        assertThat(incompleteCoursesWeek.size(), equalTo(1));
        assertThat(incompleteCoursesWeek.get(0).getId(), equalTo(COURSE_ID));
    }

    @Test
    public void incompleteCoursesMonthShouldContainCourseIfDueWithinMonth() {
        Course course = new Course();
        course.setId(COURSE_ID);

        LocalDate nextRequiredBy = LocalDate.parse("2018-01-31", FORMATTER);
        LocalDate now = LocalDate.parse("2018-01-08", FORMATTER);

        learningJob.checkAndAdd(course, identity, nextRequiredBy, now, incompleteCourses);

        assertThat(incompleteCoursesDay.size(), equalTo(0));
        assertThat(incompleteCoursesWeek.size(), equalTo(0));

        assertThat(incompleteCoursesMonth.size(), equalTo(1));
        assertThat(incompleteCoursesMonth.get(0).getId(), equalTo(COURSE_ID));
    }

    @Test
    public void incompleteCoursesShouldBeEmptyIfNotDue() {
        Course course = new Course();
        course.setId(COURSE_ID);

        LocalDate nextRequiredBy = LocalDate.parse("2018-02-20", FORMATTER);
        LocalDate now = LocalDate.parse("2018-01-01", FORMATTER);

        learningJob.checkAndAdd(course, identity, nextRequiredBy, now, incompleteCourses);

        assertThat(incompleteCoursesDay.size(), equalTo(0));
        assertThat(incompleteCoursesWeek.size(), equalTo(0));
        assertThat(incompleteCoursesMonth.size(), equalTo(0));
    }

    @Test
    public void testArgumentsOfLearningNotifications() throws NotificationClientException {
        Identity identity = new Identity();
        identity.setUid("uid");
        identity.setUsername(EMAIL);

        Course course1 = new Course();
        course1.setId("1");
        course1.setTitle(COURSE_TITLE_1);
        Course course2 = new Course();
        course2.setId("2");
        course2.setTitle(COURSE_TITLE_2);
        incompleteCoursesMonth.add(course1);
        incompleteCoursesMonth.add(course2);

        learningJob.sendNotificationForPeriod(identity, 30L, incompleteCoursesMonth);

        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> requiredLearningCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> templateIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> periodCaptor = ArgumentCaptor.forClass(String.class);

        verify(notifyService).notifyForIncompleteCourses(emailCaptor.capture(), requiredLearningCaptor.capture(), templateIdCaptor.capture(), periodCaptor.capture());
        assertThat(emailCaptor.getValue(), equalTo(EMAIL));

        String expectedRequiredLearning = COURSE_TITLE_1 + "\n" + COURSE_TITLE_2 + "\n";
        assertThat(requiredLearningCaptor.getValue(), equalTo(expectedRequiredLearning));

        assertThat(periodCaptor.getValue(), equalTo(MONTH_PERIOD));
    }

    @Test
    public void shouldNotBeAddedToDayListIfDayNotificationRecentlySent() {

        Course course = new Course();
        course.setId(COURSE_ID);
        course.setTitle(COURSE_TITLE_1);

        LocalDateTime sent = LocalDate.parse("2018-05-21", FORMATTER).atStartOfDay();

        Notification notification = new Notification(COURSE_ID, IDENTITY_UID, NotificationType.REMINDER);
        notification.setSent(sent);

        when(notificationRepository.findFirstByIdentityUidAndCourseIdAndTypeOrderBySentDesc(identity.getUid(), COURSE_ID, NotificationType.REMINDER))
                .thenReturn(Optional.of(notification));

        LocalDate now = LocalDate.parse("2018-05-22", FORMATTER);
        LocalDate requiredBy = LocalDate.parse("2018-05-22", FORMATTER);

        learningJob.checkAndAdd(course, identity, requiredBy, now, incompleteCourses);

        assertThat(incompleteCoursesDay.size(), equalTo(0));
        assertThat(incompleteCoursesWeek.size(), equalTo(0));
        assertThat(incompleteCoursesMonth.size(), equalTo(0));
    }

    @Test
    public void shouldBeAddedToWeekListIfDueInLessThanAWeekAndNotificationIsForMonth() {
        Course course = new Course();
        course.setId(COURSE_ID);
        course.setTitle(COURSE_TITLE_1);

        LocalDateTime sent = LocalDate.parse("2018-05-10", FORMATTER).atStartOfDay();

        Notification notification = new Notification(COURSE_ID, IDENTITY_UID, NotificationType.REMINDER);
        notification.setSent(sent);

        when(notificationRepository.findFirstByIdentityUidAndCourseIdAndTypeOrderBySentDesc(identity.getUid(), COURSE_ID, NotificationType.REMINDER))
                .thenReturn(Optional.of(notification));

        LocalDate now = LocalDate.parse("2018-05-22", FORMATTER);
        LocalDate requiredBy = LocalDate.parse("2018-05-25", FORMATTER);

        learningJob.checkAndAdd(course, identity, requiredBy, now, incompleteCourses);

        assertThat(incompleteCoursesDay.size(), equalTo(0));
        assertThat(incompleteCoursesWeek.size(), equalTo(1));
        assertThat(incompleteCoursesMonth.size(), equalTo(0));
    }
}
