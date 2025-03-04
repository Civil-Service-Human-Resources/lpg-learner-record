package uk.gov.cslearning.record.client.learningCatalogue;

import uk.gov.cslearning.record.service.catalogue.Course;

import java.util.List;
import java.util.Map;

public interface ILearningCatalogueClient {
    
    Map<String, List<Course>> getRequiredCoursesByDueDaysGroupedByOrg(List<String> dueDays);

    Course getCourse(String courseId);
}
