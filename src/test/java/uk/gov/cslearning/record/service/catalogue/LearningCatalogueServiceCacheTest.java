package uk.gov.cslearning.record.service.catalogue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import uk.gov.cslearning.record.service.RequestEntityFactory;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LearningCatalogueServiceCacheTest {

    @MockBean
    private RequestEntityFactory requestEntityFactory;

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private LearningCatalogueService learningCatalogueService;

    @Test
    public void shouldCacheCourseById() {
        String courseId = "course-id";
        Course course = new Course();
        course.setId(courseId);

        RequestEntity requestEntity = mock(RequestEntity.class);
        ResponseEntity<Course> responseEntity = mock(ResponseEntity.class);

        when(requestEntityFactory.createGetRequest("http://localhost:9001/courses/course-id")).thenReturn(requestEntity);
        when(restTemplate.exchange(eq(requestEntity), eq(Course.class))).thenReturn(responseEntity);
        when(responseEntity.getBody()).thenReturn(course);

        learningCatalogueService.getCourse(courseId);
        learningCatalogueService.getCourse(courseId);
        Course result = learningCatalogueService.getCourse(courseId);

        assertEquals(course, result);

        verify(requestEntityFactory, times(1)).createGetRequest("http://localhost:9001/courses/course-id");
        verify(restTemplate, times(1)).exchange(eq(requestEntity), eq(Course.class));
    }
}