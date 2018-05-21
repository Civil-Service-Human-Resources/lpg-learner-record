package uk.gov.cslearning.record.service;

import gov.adlnet.xapi.model.Account;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class LearningCatalogueService {
    private RestTemplate restTemplate;

    public LearningCatalogueService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Course> getRequiredCoursesByDepartmentCode(String departmentId){
        String plainCreds = "user:password";
        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        String base64Creds = new String(base64CredsBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);

        String url = "http://localhost:9001/courses?mandatory=true&department=" + departmentId;
        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
        Map<String, Object> responseBody = responseEntity.getBody();

        List<Course> courses = new ArrayList<>();
        if(responseBody.containsKey("results")) {
            List<Map<String, Object>> results = (List<Map<String, Object>> ) responseBody.get("results");
            for (Map<String, Object> result: results){
                Course course = new Course();
                course.setId((String) result.get("id"));
                course.setName((String) result.get("name"));
                courses.add(course);
            }
        }

        return courses;
    }
}
