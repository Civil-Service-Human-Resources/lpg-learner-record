package uk.gov.cslearning.record.service.catalogue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import uk.gov.cslearning.record.service.NotifyService;
import uk.gov.cslearning.record.service.RequestEntityException;
import uk.gov.cslearning.record.service.RequestEntityFactory;

import java.util.List;

import static java.util.Collections.emptyList;

@Service
public class LearningCatalogueService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotifyService.class);

    private final RestTemplate restTemplate;

    private final RequestEntityFactory requestEntityFactory;

    private final String courseUrlFormat;

    private final String requiredLearningUrlFormat;


    public LearningCatalogueService(RestTemplate restTemplate, RequestEntityFactory requestEntityFactory,
                                    @Value("${catalogue.courseUrlFormat}") String courseUrlFormat,
                                    @Value("${catalogue.requiredLearningUrlFormat}") String requiredLearningUrlFormat) {

        this.restTemplate = restTemplate;
        this.requestEntityFactory = requestEntityFactory;
        this.requiredLearningUrlFormat = requiredLearningUrlFormat;
        this.courseUrlFormat = courseUrlFormat;
    }

    public List<Course> getRequiredCoursesByDepartmentCode(String departmentId) {
        RequestEntity requestEntity =
                requestEntityFactory.createGetRequest(String.format(requiredLearningUrlFormat, departmentId));

        ResponseEntity<Results> responseEntity = restTemplate.exchange(requestEntity, Results.class);

        Results results = responseEntity.getBody();

        if (results != null) {
            return results.getResults();
        }
        return emptyList();
    }

    @Cacheable("courseId")
    public Course getCourse(String courseId) {
        try {
            RequestEntity requestEntity = requestEntityFactory.createGetRequest(String.format(courseUrlFormat, courseId));
            ResponseEntity<Course> responseEntity = restTemplate.exchange(requestEntity, Course.class);
            return responseEntity.getBody();
        } catch (RequestEntityException | RestClientException e) {
            LOGGER.error("Could not get course from learning catalogue: ", e.getLocalizedMessage());
            return null;
        }
    }

    public static class Results {

        private List<Course> results;

        public List<Course> getResults() {
            return results;
        }

        public void setResults(List<Course> results) {
            this.results = results;
        }
    }
}
