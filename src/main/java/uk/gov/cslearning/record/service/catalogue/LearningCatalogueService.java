package uk.gov.cslearning.record.service.catalogue;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static java.util.Collections.emptyList;

@Service
public class LearningCatalogueService {

    private RestTemplate restTemplate;

    private String courseUrlFormat;

    private String requiredLearningUrlFormat;

    private String credentials;

    public LearningCatalogueService(RestTemplate restTemplate,
                                    @Value("${catalogue.username}") String username,
                                    @Value("${catalogue.password}") String password,
                                    @Value("${catalogue.courseUrlFormat}") String courseUrlFormat,
                                    @Value("${catalogue.requiredLearningUrlFormat}") String requiredLearningUrlFormat) {
        this.restTemplate = restTemplate;
        this.requiredLearningUrlFormat = requiredLearningUrlFormat;
        this.credentials = Base64.encodeBase64String((username + ":" + password).getBytes());
        this.courseUrlFormat = courseUrlFormat;
    }

    public List<Course> getRequiredCoursesByDepartmentCode(String departmentId) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + credentials);

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<Results> responseEntity = restTemplate.exchange(
                String.format(requiredLearningUrlFormat, departmentId),
                HttpMethod.GET, request, Results.class);

        Results results = responseEntity.getBody();

        if (results != null) {
            return results.getResults();
        }
        return emptyList();
    }

    public Course getCourse(String courseId) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + credentials);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<Course> responseEntity = restTemplate.exchange(
                    String.format(courseUrlFormat, courseId),
                    HttpMethod.GET, request, Course.class);

            return responseEntity.getBody();
        } catch (Exception e) {
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
