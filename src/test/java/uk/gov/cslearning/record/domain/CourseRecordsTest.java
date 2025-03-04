package uk.gov.cslearning.record.domain;

import org.junit.jupiter.api.Test;
import uk.gov.cslearning.record.service.catalogue.LearningPeriod;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CourseRecordsTest {
    List<String> moduleIds = List.of("m1", "m2", "m3");
    LearningPeriod learningPeriod = new LearningPeriod(
            LocalDate.of(2025, 1, 1),
            LocalDate.of(2026, 1, 1)
    );
    CourseRecord courseRecord = mock(CourseRecord.class);

    @Test
    public void testCourseComplete() {
        when(courseRecord.getEarliestCompletionDateForModules(moduleIds)).thenReturn(LocalDateTime.of(
                2025, 1, 1, 10, 0, 0
        ));
        CourseRecords cs = new CourseRecords("uid", Map.of("c1", courseRecord));
        assertTrue(cs.isCourseCompleted("c1", moduleIds, learningPeriod));
    }

    @Test
    public void testCourseIncomplete() {
        when(courseRecord.getEarliestCompletionDateForModules(moduleIds)).thenReturn(LocalDateTime.of(
                2024, 12, 31, 10, 0, 0
        ));
        CourseRecords cs = new CourseRecords("uid", Map.of("c1", courseRecord));
        assertFalse(cs.isCourseCompleted("c1", moduleIds, learningPeriod));
    }

    @Test
    public void testCourseNotFound() {
        when(courseRecord.getEarliestCompletionDateForModules(moduleIds)).thenReturn(LocalDateTime.of(
                2024, 2, 1, 10, 0, 0
        ));
        CourseRecords cs = new CourseRecords("uid", Map.of("c2", courseRecord));
        assertFalse(cs.isCourseCompleted("c1", moduleIds, learningPeriod));
    }
}
