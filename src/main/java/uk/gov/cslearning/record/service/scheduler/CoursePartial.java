package uk.gov.cslearning.record.service.scheduler;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.cslearning.record.domain.CourseRecords;
import uk.gov.cslearning.record.service.catalogue.Course;
import uk.gov.cslearning.record.service.catalogue.LearningPeriod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@RequiredArgsConstructor
@Slf4j
public class CoursePartial {

    private final String courseId;
    private final String courseTitle;
    private final List<String> requiredModuleIds;
    private final List<String> departmentCodes;
    private final LearningPeriod learningPeriod;

    public static CoursePartial create(Course course, String depCode, LearningPeriod learningPeriod) {
        return new CoursePartial(course.getId(), course.getTitle(), course.getRequiredModuleIds(),
                new ArrayList<>(List.of(depCode)), learningPeriod);
    }

    public void addOrg(String depCode) {
        this.departmentCodes.add(depCode);
    }

    public List<String> getCSUidsForIncompleteLearning(Map<String, List<CourseRecords>> departmentCodesToCourseRecords) {
        List<String> uids = new ArrayList<>();
        for (String departmentCode : departmentCodes) {
            log.debug("Checking completed records for department {}", departmentCode);
            departmentCodesToCourseRecords.getOrDefault(departmentCode, List.of()).forEach(civilServantWithRecord -> {
                if (!civilServantWithRecord.isCourseCompleted(courseId, requiredModuleIds, learningPeriod)) {
                    log.debug("User {} has not completed course {} during this learning period", civilServantWithRecord.getUserId(), this.courseId);
                    uids.add(civilServantWithRecord.getUserId());
                }
            });
        }
        return uids;
    }
}
