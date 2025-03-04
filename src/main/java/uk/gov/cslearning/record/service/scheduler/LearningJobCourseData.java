package uk.gov.cslearning.record.service.scheduler;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import uk.gov.cslearning.record.domain.CourseRecords;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@RequiredArgsConstructor
public class LearningJobCourseData {

    private final LearningNotificationPeriod period;
    private final Map<String, CoursePartial> coursePartialMap;

    public Map<String, List<CourseTitleWithId>> getUidsToMissingCourses(Map<String, List<CourseRecords>> orgsWithCourseRecords) {
        Map<String, List<CourseTitleWithId>> uidsToMissingCoursesMap = new HashMap<>();
        for (CoursePartial coursePartial : coursePartialMap.values()) {
            coursePartial.getCSUidsForIncompleteLearning(orgsWithCourseRecords)
                    .forEach(uid -> {
                        List<CourseTitleWithId> missingCourses = uidsToMissingCoursesMap.getOrDefault(uid, new ArrayList<>());
                        missingCourses.add(new CourseTitleWithId(coursePartial));
                        uidsToMissingCoursesMap.put(uid, missingCourses);
                    });
        }
        return uidsToMissingCoursesMap;
    }

    @Override
    public String toString() {
        return String.format("Period: %s, courses: %s", period.getText(), coursePartialMap.size());
    }
}
