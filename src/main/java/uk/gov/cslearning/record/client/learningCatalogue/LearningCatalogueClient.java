package uk.gov.cslearning.record.client.learningCatalogue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.client.IHttpClient;
import uk.gov.cslearning.record.service.catalogue.Course;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class LearningCatalogueClient implements ILearningCatalogueClient {
    private final IHttpClient httpClient;
    private final String courseUrlFormat;
    private final String requiredLearningUrlByDaysFormat;

    public LearningCatalogueClient(@Qualifier("learningCatalogueHttpClient") IHttpClient httpClient,
                                   @Value("${catalogue.courseUrlFormat}") String courseUrlFormat,
                                   @Value("${catalogue.requiredLearningUrlByDaysFormat}") String requiredLearningUrlByDaysFormat) {
        this.httpClient = httpClient;
        this.courseUrlFormat = courseUrlFormat;
        this.requiredLearningUrlByDaysFormat = requiredLearningUrlByDaysFormat;
    }

    @Override
    public Map<String, List<Course>> getRequiredCoursesByDueDaysGroupedByOrg(List<String> dueDays) {
        String joinedDueDays = String.join(",", dueDays);
        log.info(String.format("Fetching mandatory courses with deadlines of %s days", joinedDueDays));
        String url = String.format(requiredLearningUrlByDaysFormat, joinedDueDays);
        RequestEntity<Void> request = RequestEntity
                .get(url)
                .build();
        return httpClient.executeMapRequest(request, new ParameterizedTypeReference<>() {
        });
    }

    @Override
    public Course getCourse(String courseId) {
        String url = String.format(courseUrlFormat, courseId);
        RequestEntity<Void> request = RequestEntity
                .get(url)
                .build();
        return httpClient.executeRequest(request, Course.class);
    }
}
