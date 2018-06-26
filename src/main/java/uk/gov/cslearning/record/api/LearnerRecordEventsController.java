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
import uk.gov.cslearning.record.domain.BookingStatus;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.domain.State;
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
@RequestMapping("/events")
//@PreAuthorize("hasAnyAuthority('DOWNLOAD_BOOKING_FEED')")
public class LearnerRecordEventsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LearnerRecordEventsController.class);

    private LearningCatalogueService learningCatalogueService;

    private CourseRecordRepository courseRecordRepository;

    @Autowired
    public LearnerRecordEventsController(LearningCatalogueService learningCatalogueService,
                                         CourseRecordRepository courseRecordRepository) {
        checkArgument(learningCatalogueService != null);
        checkArgument(courseRecordRepository != null);
        this.learningCatalogueService = learningCatalogueService;
        this.courseRecordRepository = courseRecordRepository;
    }

    @GetMapping
    public ResponseEntity<Collection<LearnerRecordEvents>> list() {

        Iterable<CourseRecord> records = courseRecordRepository.listEventRecords();
        if (records == null) {
            LOGGER.info("No event records returned.");
            return ResponseEntity.badRequest().build();
        }

        Map<String, LearnerRecordEvents> events = new HashMap<>();

        for (CourseRecord courseRecord : records) {

            for (ModuleRecord moduleRecord : courseRecord.getModuleRecords()) {

                String key = String.format("%s-%s", courseRecord.getCourseId(), moduleRecord.getModuleId());

                Course course = learningCatalogueService.getCourse(courseRecord.getCourseId());
                Module module = course.getModule(moduleRecord.getModuleId());

                if (course == null || module == null) {
                    LOGGER.warn("Course or module not found for courseId {}, moduleId {}.", courseRecord.getCourseId(),
                            moduleRecord.getModuleId());
                }

                LearnerRecordEvents event = events.computeIfAbsent(key, s -> {
                    LearnerRecordEvents newEvent = new LearnerRecordEvents();
                    newEvent.setBookingReference("BookingRef123");
                    newEvent.setCourseName(course.getTitle());
                    newEvent.setCourseId(course.getId());
                    newEvent.setStatus(BookingStatus.REQUESTED);


                    return newEvent;
                });
            }
        }
        return new ResponseEntity<>(events.values(), OK);
    }
}
