package uk.gov.cslearning.record.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cslearning.record.csrs.domain.CivilServant;
import uk.gov.cslearning.record.csrs.service.RegistryService;
import uk.gov.cslearning.record.domain.*;
import uk.gov.cslearning.record.repository.BookingRepository;
import uk.gov.cslearning.record.repository.CourseRecordRepository;
import uk.gov.cslearning.record.repository.EventRepository;
import uk.gov.cslearning.record.repository.LearnerRepository;
import uk.gov.cslearning.record.security.SecurityUtil;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/summaries")
@PreAuthorize("hasAnyAuthority('ORGANISATION_REPORTER', 'PROFESSION_REPORTER', 'CSHR_REPORTER')")
public class LearnerRecordSummaryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LearnerRecordSummaryController.class);

    private static final String CSHR_REPORTER = "CSHR_REPORTER";

    private static final String ORGANISATION_REPORTER = "ORGANISATION_REPORTER";

    private static final String PROFESSION_REPORTER = "PROFESSION_REPORTER";

    private RegistryService registryService;

    private CourseRecordRepository courseRecordRepository;

    @Autowired
    public LearnerRecordSummaryController(RegistryService registryService,
                                          CourseRecordRepository courseRecordRepository) {
        checkArgument(registryService != null);
        checkArgument(courseRecordRepository != null);
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

                LearnerRecordSummary summary = summaries.computeIfAbsent(key, s -> {
                    LearnerRecordSummary newSummary = new LearnerRecordSummary();
                    newSummary.setCourseIdentifier(courseRecord.getCourseId());
                    newSummary.setCourseName(courseRecord.getCourseTitle());
                    newSummary.setModuleIdentifier(moduleRecord.getModuleId());
                    newSummary.setModuleName(moduleRecord.getModuleTitle());
                    newSummary.setType(moduleRecord.getModuleType());
                    newSummary.setTimeTaken(moduleRecord.getDuration());
                    return newSummary;
                });

                if (moduleRecord.getState() != null) {
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
                } else {
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

        Optional<CivilServant> optionalCivilServant = registryService.getCurrent();

        if (!optionalCivilServant.isPresent()) {
            throw new AccessDeniedException("No civil servant details found.");
        }

        if (SecurityUtil.hasAuthority(PROFESSION_REPORTER)) {
            return courseRecordRepository.findByProfession(optionalCivilServant.get().getProfession().getName());
        }

        if (SecurityUtil.hasAuthority(ORGANISATION_REPORTER)) {
            return courseRecordRepository.findByDepartment(optionalCivilServant.get().getOrganisationalUnit().getCode());
        }
        return null;
    }
}
