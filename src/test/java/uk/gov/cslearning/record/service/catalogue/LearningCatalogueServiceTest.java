package uk.gov.cslearning.record.service.catalogue;

import org.junit.Test;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import uk.gov.cslearning.record.service.RequestEntityFactory;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LearningCatalogueServiceTest {

    private RestTemplate restTemplate = mock(RestTemplate.class);
    private RequestEntityFactory requestEntityFactory = mock(RequestEntityFactory.class);
    private String courseUrlFormat = "course %s";
    private String requiredLearningUrlFormat = "department %s";
    private String requiredLearningUrlByDaysFormat = "days= s";

    private LearningCatalogueService learningCatalogueService =
            new LearningCatalogueService(restTemplate, requestEntityFactory, courseUrlFormat, requiredLearningUrlFormat, requiredLearningUrlByDaysFormat);


    @Test
    public void shouldRequestCourseByIdFromLearningCatalogue() {
        String courseId = "course-id";
        Course course = new Course();

        RequestEntity requestEntity = mock(RequestEntity.class);
        ResponseEntity<Course> responseEntity = mock(ResponseEntity.class);

        when(requestEntityFactory.createGetRequest("course course-id")).thenReturn(requestEntity);
        when(restTemplate.exchange(eq(requestEntity), eq(Course.class))).thenReturn(responseEntity);
        when(responseEntity.getBody()).thenReturn(course);

        Course result = learningCatalogueService.getCourse(courseId);

        assertEquals(course, result);
    }

    @Test
    public void shouldReturnCoursesByDepartmentCode() {
        String departmentId = "department-id";
        List<Course> courseList = Collections.singletonList(new Course());

        RequestEntity requestEntity = mock(RequestEntity.class);
        ResponseEntity<LearningCatalogueService.Results> responseEntity = mock(ResponseEntity.class);
        LearningCatalogueService.Results results = mock(LearningCatalogueService.Results.class);

        when(requestEntityFactory.createGetRequest("department department-id")).thenReturn(requestEntity);
        when(restTemplate.exchange(eq(requestEntity), eq(LearningCatalogueService.Results.class))).thenReturn(responseEntity);
        when(responseEntity.getBody()).thenReturn(results);
        when(results.getResults()).thenReturn(courseList);

        List<Course> result = learningCatalogueService.getRequiredCoursesByDepartmentCode(departmentId);

        assertEquals(courseList, result);
    }

    @Test
    public void shouldReturnEmptyListIfCoursesFoundForDepartment() {
        String departmentId = "department-id";

        RequestEntity requestEntity = mock(RequestEntity.class);
        ResponseEntity<LearningCatalogueService.Results> responseEntity = mock(ResponseEntity.class);

        when(requestEntityFactory.createGetRequest("department department-id")).thenReturn(requestEntity);
        when(restTemplate.exchange(eq(requestEntity), eq(LearningCatalogueService.Results.class))).thenReturn(responseEntity);
        when(responseEntity.getBody()).thenReturn(null);

        List<Course> result = learningCatalogueService.getRequiredCoursesByDepartmentCode(departmentId);

        assertEquals(Collections.emptyList(), result);
    }
}