package uk.gov.cslearning.record.service.scheduler.learningJob;

import org.junit.jupiter.api.Test;
import uk.gov.cslearning.record.domain.CourseRecords;
import uk.gov.cslearning.record.service.scheduler.CoursePartial;
import uk.gov.cslearning.record.service.scheduler.CourseTitleWithId;
import uk.gov.cslearning.record.service.scheduler.LearningJobCourseData;
import uk.gov.cslearning.record.service.scheduler.LearningNotificationPeriod;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LearningJobCourseDataTest {

    @Test
    public void shouldReturnCivilServantUidsWithMissingCourses() {
        CoursePartial course1Mock = mock(CoursePartial.class);
        when(course1Mock.getCSUidsForIncompleteLearning(any())).thenReturn(List.of("uid1", "uid4"));
        when(course1Mock.getCourseId()).thenReturn("course1");
        when(course1Mock.getCourseTitle()).thenReturn("course 1");

        CoursePartial course2Mock = mock(CoursePartial.class);
        when(course2Mock.getCourseId()).thenReturn("course2");
        when(course2Mock.getCourseTitle()).thenReturn("course 2");
        when(course2Mock.getCSUidsForIncompleteLearning(any())).thenReturn(List.of("uid1", "uid3", "uid4"));

        CoursePartial course3Mock = mock(CoursePartial.class);
        when(course3Mock.getCourseId()).thenReturn("course3");
        when(course3Mock.getCourseTitle()).thenReturn("course 3");
        when(course3Mock.getCSUidsForIncompleteLearning(any())).thenReturn(List.of("uid1", "uid2", "uid4"));
        Map<String, CoursePartial> coursePartialMap = Map.of(
                "course1", course1Mock, "course2", course2Mock, "course3", course3Mock
        );
        LearningJobCourseData courseData = new LearningJobCourseData(new LearningNotificationPeriod("test", 1L), coursePartialMap);

        Map<String, List<CourseRecords>> civilServants = Map.of(
                "org1", List.of(),
                "org2", List.of(),
                "org3", List.of()
        );

        Map<String, List<CourseTitleWithId>> result = courseData.getUidsToMissingCourses(civilServants);
        assertEquals(3, result.get("uid1").size());
        assertEquals(1, result.get("uid2").size());
        assertEquals(1, result.get("uid3").size());
        assertEquals(3, result.get("uid4").size());
    }

}
