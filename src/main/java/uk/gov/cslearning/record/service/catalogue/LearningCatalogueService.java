package uk.gov.cslearning.record.service.catalogue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.client.learningCatalogue.ILearningCatalogueClient;
import uk.gov.cslearning.record.csrs.service.RegistryService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class LearningCatalogueService {

    private final ILearningCatalogueClient learningCatalogueClient;
    private final CourseDataFactory courseDataFactory;

    private final RegistryService registryService;

    public LearningCatalogueService(ILearningCatalogueClient learningCatalogueClient,
                                    CourseDataFactory courseDataFactory, RegistryService registryService) {
        this.learningCatalogueClient = learningCatalogueClient;
        this.courseDataFactory = courseDataFactory;
        this.registryService = registryService;
    }

    public Map<String, List<RequiredCourse>> getRequiredCoursesByDueDaysGroupedByOrg(List<Long> dueDays) {
        Map<String, List<RequiredCourse>> response = new HashMap<>();
        Map<String, List<RequiredCourse>> orgsToRequiredLearning = new HashMap<>();
        List<List<String>> hierarchies = registryService.getOrganisationCodeHierarchy();
        learningCatalogueClient.getRequiredCoursesByDueDaysGroupedByOrg(dueDays.stream().map(Object::toString).toList())
                .forEach((department, courses) -> {
                    List<RequiredCourse> transformedCourses = courses.stream().map(c -> courseDataFactory.transformCourse(c, hierarchies)).toList();
                    orgsToRequiredLearning.putIfAbsent(department, transformedCourses);
                });
        for (List<String> hierarchy : hierarchies) {
            for (String org : hierarchy) {
                List<RequiredCourse> requiredLearningForOrg = orgsToRequiredLearning.get(org);
                if (requiredLearningForOrg != null) {
                    List<RequiredCourse> coursesToAdd = new ArrayList<>();
                    List<String> coursesAdded = new ArrayList<>();
                    for (RequiredCourse requiredCourse : requiredLearningForOrg) {
                        if (!coursesAdded.contains(requiredCourse.getId())) {
                            coursesToAdd.add(requiredCourse);
                            coursesAdded.add(requiredCourse.getId());
                        }
                    }
                    response.put(hierarchy.get(0), coursesToAdd);
                }
            }
        }
        return response;
    }

    public Course getCourse(String courseId) {
        return learningCatalogueClient.getCourse(courseId);
    }

}
