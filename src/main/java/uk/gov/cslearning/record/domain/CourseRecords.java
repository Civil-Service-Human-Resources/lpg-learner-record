package uk.gov.cslearning.record.domain;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import uk.gov.cslearning.record.service.catalogue.LearningPeriod;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@RequiredArgsConstructor
public class CourseRecords {

    private final String userId;
    private final Map<String, CourseRecord> courseRecords;

    public static CourseRecords create(String userId, List<CourseRecord> courseRecords) {
        return new CourseRecords(userId, courseRecords.stream().collect(Collectors.toMap(CourseRecord::getCourseId, cr -> cr)));
    }

    public Optional<CourseRecord> getCourseRecord(String courseId) {
        return Optional.ofNullable(this.courseRecords.get(courseId));
    }

    public boolean isCourseCompleted(String courseId, List<String> requiredModuleIds, LearningPeriod learningPeriod) {
        return getCourseRecord(courseId)
                .map(cr -> {
                    LocalDate earliestCompletionDate = cr.getEarliestCompletionDateForModules(requiredModuleIds).toLocalDate();
                    LocalDate lpStartDate = Objects.requireNonNullElse(learningPeriod.getStartDate(), LocalDate.MIN);
                    return !lpStartDate.isAfter(earliestCompletionDate);
                }).orElse(false);
    }

}
