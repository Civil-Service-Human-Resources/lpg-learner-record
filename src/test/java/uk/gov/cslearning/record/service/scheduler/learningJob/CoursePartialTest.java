package uk.gov.cslearning.record.service.scheduler.learningJob;

import org.junit.jupiter.api.Test;
import uk.gov.cslearning.record.domain.CourseRecords;
import uk.gov.cslearning.record.service.catalogue.LearningPeriod;
import uk.gov.cslearning.record.service.scheduler.CoursePartial;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CoursePartialTest {

    @Test
    public void fetchUserIdsForCompleteAndIncompleteCourses() {
        String courseId = "course1";
        List<String> modules = List.of("mod1", "mod2", "mod3");
        LearningPeriod lp = new LearningPeriod(LocalDate.of(2024, 1, 1),
                LocalDate.of(2025, 1, 1));

        CoursePartial coursePartial = new CoursePartial(
                courseId, "course 1", modules,
                List.of("CO", "HMRC"), lp);

        CourseRecords user1 = mock(CourseRecords.class);
        when(user1.getUserId()).thenReturn("uid1");
        when(user1.isCourseCompleted(courseId, modules, lp)).thenReturn(false);

        CourseRecords user2 = mock(CourseRecords.class);
        when(user2.getUserId()).thenReturn("uid2");
        when(user2.isCourseCompleted(courseId, modules, lp)).thenReturn(true);

        CourseRecords user3 = mock(CourseRecords.class);
        when(user3.getUserId()).thenReturn("uid3");
        when(user3.isCourseCompleted(courseId, modules, lp)).thenReturn(false);

        CourseRecords user4 = mock(CourseRecords.class);
        when(user4.getUserId()).thenReturn("uid4");
        when(user4.isCourseCompleted(courseId, modules, lp)).thenReturn(true);

        Map<String, List<CourseRecords>> civilServants = Map.of(
                "HMRC", List.of(user1, user2, user3),
                "CO", List.of(user4)
        );

        List<String> resultUids = coursePartial.getCSUidsForIncompleteLearning(civilServants);
        assertEquals(List.of("uid1", "uid3"), resultUids);
    }

}
