package uk.gov.cslearning.record.service.catalogue;

import static java.util.Collections.emptyList;

import java.util.List;
import java.util.Map;

import uk.gov.cslearning.record.service.NotifyService;
import uk.gov.cslearning.record.service.RequestEntityException;
import uk.gov.cslearning.record.service.RequestEntityFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class LearningCatalogueService {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotifyService.class);

    private final RestTemplate restTemplate;

    private final RequestEntityFactory requestEntityFactory;

    private final String courseUrlFormat;

    private final String requiredLearningUrlFormat;

    private final String requiredLearningUrlByDaysFormat;

    public LearningCatalogueService(RestTemplate restTemplate, RequestEntityFactory requestEntityFactory,
                                    @Value("${catalogue.courseUrlFormat}") String courseUrlFormat,
                                    @Value("${catalogue.requiredLearningUrlFormat}") String requiredLearningUrlFormat,
                                    @Value("${catalogue.requiredLearningUrlByDaysFormat}") String requiredLearningUrlByDaysFormat) {

        this.restTemplate = restTemplate;
        this.requestEntityFactory = requestEntityFactory;
        this.requiredLearningUrlFormat = requiredLearningUrlFormat;
        this.courseUrlFormat = courseUrlFormat;
        this.requiredLearningUrlByDaysFormat = requiredLearningUrlByDaysFormat;
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

    public Map<String, List<Course>> getRequiredCoursesByDueDaysGroupedByOrg(String dueDays) {
        RequestEntity requestEntity = requestEntityFactory.createGetRequest(String.format(requiredLearningUrlByDaysFormat, dueDays));

        ResponseEntity<Map<String, List<Course>>> responseEntity = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<Map<String, List<Course>>>(){});

        return responseEntity.getBody();
    }

    public Course getCourse(String courseId) {
        try {
            LOGGER.debug("This function doesn't cache a course");
            RequestEntity requestEntity = requestEntityFactory.createGetRequest(String.format(courseUrlFormat, courseId));
            ResponseEntity<Course> responseEntity = restTemplate.exchange(requestEntity, Course.class);
            return responseEntity.getBody();
        } catch (RequestEntityException | RestClientException e) {
            LOGGER.error("Could not get course from learning catalogue: ", e.getLocalizedMessage());
            return null;
        }
    }

    @Cacheable("courseId")
    public Course getCachedCourse(String courseId) {
        try {
            LOGGER.debug("This function caches a course which used for statements");
            RequestEntity requestEntity = requestEntityFactory.createGetRequest(String.format(courseUrlFormat, courseId));
            ResponseEntity<Course> responseEntity = restTemplate.exchange(requestEntity, Course.class);
            return responseEntity.getBody();
        } catch (RequestEntityException | RestClientException e) {
            LOGGER.error("Could not get course from learning catalogue: ", e.getLocalizedMessage());
            return null;
        }
    }

    public Event getEventByUrl(String url) {
        try{
            RequestEntity requestEntity = requestEntityFactory.createGetRequest(url);
            ResponseEntity<Event> responseEntity = restTemplate.exchange(requestEntity, Event.class);
            return responseEntity.getBody();
        } catch (RequestEntityException | RestClientException e) {
            LOGGER.error("Could not get event from learning catalogue: ", e.getLocalizedMessage());
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
