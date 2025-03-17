package uk.gov.cslearning.record.service.scheduler;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.cslearning.record.domain.CourseRecords;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@RequiredArgsConstructor
@Slf4j
public class LearningJobCourseData {

    private final LearningNotificationPeriod period;
    private final Map<String, CoursePartial> coursePartialMap;

    public Map<String, List<CourseTitleWithId>> getUidsToMissingCourses(Map<String, List<CourseRecords>> orgsWithCourseRecords) {
        Map<String, List<CourseTitleWithId>> uidsToMissingCoursesMap = new HashMap<>();
        for (CoursePartial coursePartial : coursePartialMap.values()) {
            List<String> usersWithIncompleteRecords = coursePartial.getCSUidsForIncompleteLearning(orgsWithCourseRecords);
            log.debug("{} users haven't completed course {}", usersWithIncompleteRecords.size(), coursePartial.getCourseTitle());
            usersWithIncompleteRecords
                    .forEach(uid -> {
                        CourseTitleWithId courseTitleWithId = new CourseTitleWithId(coursePartial);
                        log.debug("Adding course {} to incompleted list for user {}", courseTitleWithId, uid);
                        List<CourseTitleWithId> missingCourses = uidsToMissingCoursesMap.getOrDefault(uid, new ArrayList<>());
                        missingCourses.add(courseTitleWithId);
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
