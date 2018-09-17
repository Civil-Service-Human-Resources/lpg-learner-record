package uk.gov.cslearning.record.service.catalogue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static java.util.Collections.emptyList;

@Service
public class LearningCatalogueService {

    private RestTemplate restTemplate;

    private String courseUrlFormat;

    private String requiredLearningUrlFormat;

    private HttpHeaders headers;

    public LearningCatalogueService(RestTemplate restTemplate,
                                    @Value("${catalogue.courseUrlFormat}") String courseUrlFormat,
                                    @Value("${catalogue.requiredLearningUrlFormat}") String requiredLearningUrlFormat) {

        this.restTemplate = restTemplate;
        this.requiredLearningUrlFormat = requiredLearningUrlFormat;
        this.courseUrlFormat = courseUrlFormat;
    }

    public List<Course> getRequiredCoursesByDepartmentCode(String departmentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) authentication.getDetails();

        headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + details.getTokenValue());

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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) authentication.getDetails();

        headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + details.getTokenValue());

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
