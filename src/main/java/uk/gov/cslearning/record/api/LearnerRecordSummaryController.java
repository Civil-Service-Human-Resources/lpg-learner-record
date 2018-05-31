package uk.gov.cslearning.record.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.repository.CourseRecordRepository;
import uk.gov.cslearning.record.security.SecurityUtil;
import uk.gov.cslearning.record.service.CivilServant;
import uk.gov.cslearning.record.service.RegistryService;
import uk.gov.cslearning.record.service.catalogue.Course;
import uk.gov.cslearning.record.service.catalogue.LearningCatalogueService;
import uk.gov.cslearning.record.service.catalogue.Module;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/summaries")
@PreAuthorize("hasAnyAuthority('ORGANISATION_REPORTER', 'PROFESSION_REPORTER', 'CSHR_REPORTER')")
public class LearnerRecordSummaryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LearnerRecordSummaryController.class);

    private static final String CSHR_REPORTER = "CSHR_REPORTER";

    private static final String DEPARTMENT_REPORTER = "DEPARTMENT_REPORTER";

    private static final String PROFESSION_REPORTER = "PROFESSION_REPORTER";

    private LearningCatalogueService learningCatalogueService;

    private RegistryService registryService;

    private CourseRecordRepository courseRecordRepository;

    @Autowired
    public LearnerRecordSummaryController(LearningCatalogueService learningCatalogueService,
                                          RegistryService registryService,
                                          CourseRecordRepository courseRecordRepository) {
        checkArgument(learningCatalogueService != null);
        checkArgument(registryService != null);
        checkArgument(courseRecordRepository != null);
        this.learningCatalogueService = learningCatalogueService;
        this.registryService = registryService;
        this.courseRecordRepository = courseRecordRepository;
    }

    @GetMapping
    public ResponseEntity<Collection<LearnerRecordSummary>> list() {

        Iterable<CourseRecord> records = getRecords();
        if (records == null) {
            LOGGER.info("No course records returned for user, may have no department or profession set.");
            return ResponseEntity.badRequest().build();
        }

        Map<String, LearnerRecordSummary> summaries = new HashMap<>();

        for (CourseRecord courseRecord : records) {

            for (ModuleRecord moduleRecord : courseRecord.getModuleRecords()) {

                String key = String.format("%s-%s", courseRecord.getCourseId(), moduleRecord.getModuleId());

                Course course = learningCatalogueService.getCourse(courseRecord.getCourseId());
                Module module = course.getModule(moduleRecord.getModuleId());

                if (course == null || module == null) {
                    LOGGER.warn("Course or module not found for courseId {}, moduleId {}.", courseRecord.getCourseId(),
                            moduleRecord.getModuleId());
                }

                LearnerRecordSummary summary = summaries.computeIfAbsent(key, s -> {
                    LearnerRecordSummary newSummary = new LearnerRecordSummary();
                    newSummary.setCourseIdentifier(courseRecord.getCourseId());
                    newSummary.setCourseName(course.getTitle());
                    newSummary.setModuleIdentifier(moduleRecord.getModuleId());
                    newSummary.setModuleName(module.getTitle());
                    newSummary.setType(module.getModuleType());
                    newSummary.setTimeTaken(module.getDuration());
                    return newSummary;
                });

                switch (moduleRecord.getState()) {
                    case COMPLETED:
                        summary.incrementCompleted();
                        break;
                    case IN_PROGRESS:
                        summary.incrementInProgress();
                        break;
                    case ARCHIVED:
                    case UNREGISTERED:
                        break;
                    default:
                        summary.incrementNotStarted();
                }
            }
        }
        return new ResponseEntity<>(summaries.values(), OK);
    }

    private Iterable<CourseRecord> getRecords() {

        if (SecurityUtil.hasAuthority(CSHR_REPORTER)) {
            return courseRecordRepository.findAll();
        }

        CivilServant civilServant = registryService.getCurrent();

        if (civilServant == null) {
            throw new AccessDeniedException("No civil servant details found.");
        }

        if (SecurityUtil.hasAuthority(PROFESSION_REPORTER)) {
            return courseRecordRepository.findByProfession(civilServant.getProfession());
        }

        if (SecurityUtil.hasAuthority(DEPARTMENT_REPORTER)) {
            return courseRecordRepository.findByDepartment(civilServant.getDepartmentCode());
        }
        return null;
    }
}
