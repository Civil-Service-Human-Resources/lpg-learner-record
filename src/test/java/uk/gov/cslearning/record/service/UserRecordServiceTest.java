package uk.gov.cslearning.record.service;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import gov.adlnet.xapi.model.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.State;
import uk.gov.cslearning.record.repository.CourseRecordRepository;
import uk.gov.cslearning.record.service.xapi.ActivityType;
import uk.gov.cslearning.record.service.xapi.XApiService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static uk.gov.cslearning.record.service.xapi.activity.Activity.COURSE_ID_PREFIX;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class UserRecordServiceTest {

    private UserRecordService userRecordService;

    @Mock
    private XApiService xApiService;

    @Mock
    private RegistryService registryService;

    @Autowired
    private CourseRecordRepository courseRecordRepository;

    @Before
    public void setup() {
        userRecordService = new UserRecordService(courseRecordRepository, xApiService, registryService);
    }

    @Test
    public void shouldUpdateExistingRecord() throws Exception {

        final String userId = "userId";
        final String courseId = COURSE_ID_PREFIX + "/courseId";
        final String activityId = courseId;

        CourseRecord courseRecord = new CourseRecord(courseId, "userId");
        courseRecordRepository.save(courseRecord);

        Statement statement = createStatement(activityId, uk.gov.cslearning.record.service.xapi.Verb.ARCHIVED);

        when(xApiService.getStatements(eq(userId), eq(null), any())).thenReturn(ImmutableSet.of(statement));
        when(registryService.getCivilServantByUid(userId)).thenReturn(Optional.of(new CivilServant()));

        Collection<CourseRecord> courseRecords = userRecordService.getUserRecord(userId, activityId);

        assertThat(courseRecords.size(), is(1));

        CourseRecord updatedCourseRecord = Iterables.get(courseRecords, 0);

        assertThat(updatedCourseRecord.getCourseId(), equalTo(courseId));
        assertThat(updatedCourseRecord.getUserId(), equalTo(userId));
        assertThat(updatedCourseRecord.getState(), equalTo(State.ARCHIVED));
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