package uk.gov.cslearning.record.service;

import org.junit.Test;
import org.springframework.web.client.RestTemplate;
import uk.gov.cslearning.record.service.catalogue.LearningCatalogueService;

public class LearningCatalogueServiceTest {

    private RestTemplate restTemplate;

    @Test
    public void getCourses() {
        restTemplate = new RestTemplate();

        LearningCatalogueService learningCatalogueService = new LearningCatalogueService(restTemplate);
        learningCatalogueService.getRequiredCoursesByDepartmentCode("co");
    }
}