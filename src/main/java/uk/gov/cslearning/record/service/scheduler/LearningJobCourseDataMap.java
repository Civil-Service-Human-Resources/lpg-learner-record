package uk.gov.cslearning.record.service.scheduler;

import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.gov.cslearning.record.service.catalogue.Course;
import uk.gov.cslearning.record.service.catalogue.LearningPeriod;

import java.util.*;

@EqualsAndHashCode(callSuper = true)
@Data
public class LearningJobCourseDataMap extends HashMap<Long, LearningJobCourseData> {

    private final Set<String> orgCodes;
    private final Set<String> courseIds;

    public LearningJobCourseDataMap() {
        this.orgCodes = new HashSet<>();
        this.courseIds = new HashSet<>();
    }

    public static LearningJobCourseDataMap create(List<LearningNotificationPeriod> deadlineDays) {
        LearningJobCourseDataMap courseData = new LearningJobCourseDataMap();
        deadlineDays.forEach(period -> courseData.put(period.getDays(), new LearningJobCourseData(period, new HashMap<>())));
        return courseData;
    }

    public void addCourseAndDepartment(LearningNotificationPeriod period, Course course, String depCode, LearningPeriod learningPeriod) {
        Map<String, CoursePartial> coursePartialMap = this.get(period.getDays()).getCoursePartialMap();
        coursePartialMap.merge(course.getId(), CoursePartial.create(course, depCode, learningPeriod), (existing, incoming) -> {
            existing.addOrg(depCode);
            return existing;
        });
        this.orgCodes.add(depCode);
        this.courseIds.add(course.getId());
    }

    @Override
    public String toString() {
        return String.format("%s courses across %s organisations", courseIds.size(), orgCodes.size());
    }

}
