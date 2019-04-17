package uk.gov.cslearning.record.service;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import gov.adlnet.xapi.model.Activity;
import gov.adlnet.xapi.model.ActivityDefinition;
import gov.adlnet.xapi.model.Statement;
import gov.adlnet.xapi.model.Verb;
import org.assertj.core.util.Lists;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.csrs.domain.CivilServant;
import uk.gov.cslearning.record.csrs.service.RegistryService;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.State;
import uk.gov.cslearning.record.repository.CourseRecordRepository;
import uk.gov.cslearning.record.service.catalogue.Course;
import uk.gov.cslearning.record.service.catalogue.LearningCatalogueService;
import uk.gov.cslearning.record.service.xapi.ActivityType;
import uk.gov.cslearning.record.service.xapi.XApiService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.cslearning.record.service.xapi.activity.Activity.COURSE_ID_PREFIX;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class UserRecordServiceTest {

    private UserRecordService userRecordService;

    @Mock
    private LearningCatalogueService learningCatalogueService;

    @Mock
    private XApiService xApiService;

    @Mock
    private RegistryService registryService;

    @Mock
    private CourseRecordRepository courseRecordRepository;

    @Mock
    private CollectionsService collectionsService;

    @Before
    public void setup() {
        userRecordService = new UserRecordService(courseRecordRepository, xApiService, registryService,
                learningCatalogueService, collectionsService);
    }

    @Test
    public void shouldUpdateExistingRecord() throws Exception {

        final String userId = "userId";
        final String courseId = "courseId";
        final String activityId = COURSE_ID_PREFIX + "/" + courseId;

        CourseRecord courseRecord = new CourseRecord(courseId, "userId");
        courseRecordRepository.save(courseRecord);

        ArrayList<CourseRecord> savedCourseRecords = new ArrayList<>();
        savedCourseRecords.add(courseRecord);

        Statement statement = createStatement(activityId, uk.gov.cslearning.record.service.xapi.Verb.ARCHIVED);

        when(learningCatalogueService.getCourse(eq(courseId))).thenReturn(createCourse(courseId));
        when(xApiService.getStatements(eq(userId), eq(null), any())).thenReturn(ImmutableSet.of(statement));
        when(registryService.getCivilServantByUid(userId)).thenReturn(Optional.of(new CivilServant()));
        when(courseRecordRepository.findByUserId(userId)).thenReturn(savedCourseRecords);

        Collection<CourseRecord> courseRecords = userRecordService.getUserRecord(userId, Lists.newArrayList(activityId));

        assertThat(courseRecords.size(), is(1));

        CourseRecord updatedCourseRecord = Iterables.get(courseRecords, 0);

        assertThat(updatedCourseRecord.getCourseId(), equalTo(courseId));
        assertThat(updatedCourseRecord.getUserId(), equalTo(userId));
        assertThat(updatedCourseRecord.getState(), equalTo(State.ARCHIVED));
    }

    @Test
    public void shouldDeleteUserRecords() throws Exception {
        String uid = "userId";

        userRecordService.deleteUserRecords(uid);

        verify(collectionsService).deleteAllByLearnerUid(uid);
        verify(courseRecordRepository).deleteAllByUid(uid);
    }

    @Test
    public void shouldDeleteStatementsOlderThanDateTime() {
        DateTime dateTime = DateTime.now().minusMonths(36);
        Instant instant = DateTimeFormatter.ISO_DATE_TIME.parse(dateTime.toString(), Instant::from);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        userRecordService.deleteOldRecords(dateTime, localDateTime);

        verify(collectionsService).deleteAllByAge(any());
    }

    private Course createCourse(String courseId) {
        Course course = new Course();
        course.setId(courseId);
        return course;
    }

    private Statement createStatement(String activityId, uk.gov.cslearning.record.service.xapi.Verb verb) {

        ActivityDefinition activityDefinition = new ActivityDefinition();
        activityDefinition.setType(ActivityType.COURSE.getUri());

        Activity activity = new Activity(activityId, activityDefinition);
        Statement statement = new Statement(null, new Verb(verb.getUri()), activity);
        statement.setTimestamp(XApiService.DATE_FORMATTER.format(LocalDateTime.now()));

        return statement;
    }
}