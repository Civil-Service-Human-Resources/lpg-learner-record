package uk.gov.cslearning.record.service.catalogue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.client.learningCatalogue.ILearningCatalogueClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class LearningCatalogueService {

    private final ILearningCatalogueClient learningCatalogueClient;
    private final CourseDataFactory courseDataFactory;

    public LearningCatalogueService(ILearningCatalogueClient learningCatalogueClient,
                                    CourseDataFactory courseDataFactory) {
        this.learningCatalogueClient = learningCatalogueClient;
        this.courseDataFactory = courseDataFactory;
    }

    public Map<String, List<RequiredCourse>> getRequiredCoursesByDueDaysGroupedByOrg(List<Long> dueDays) {
        Map<String, List<RequiredCourse>> response = new HashMap<>();
        learningCatalogueClient.getRequiredCoursesByDueDaysGroupedByOrg(dueDays.stream().map(Object::toString).toList())
                .forEach((department, courses) -> {
                    List<RequiredCourse> transformedCourses = courses.stream().map(courseDataFactory::transformCourse).toList();
                    response.putIfAbsent(department, transformedCourses);
                });
        return response;
    }

    public Course getCourse(String courseId) {
        return learningCatalogueClient.getCourse(courseId);
    }

}
