package uk.gov.cslearning.record.api;

import org.apache.commons.lang3.StringUtils;
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
import uk.gov.cslearning.record.service.catalogue.Event;
import uk.gov.cslearning.record.service.catalogue.LearningCatalogueService;
import uk.gov.cslearning.record.service.catalogue.Module;
import uk.gov.cslearning.record.service.identity.Identity;
import uk.gov.cslearning.record.service.identity.IdentityService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/events")
@PreAuthorize("hasAnyAuthority('DOWNLOAD_BOOKING_FEED')")
public class LearnerRecordEventsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LearnerRecordEventsController.class);

    private LearningCatalogueService learningCatalogueService;

    private CourseRecordRepository courseRecordRepository;

    private IdentityService identityService;

    private RegistryService registryService;

    @Autowired
    public LearnerRecordEventsController(LearningCatalogueService learningCatalogueService,
                                         CourseRecordRepository courseRecordRepository,
                                         IdentityService identityService,
                                         RegistryService registryService) {
        checkArgument(learningCatalogueService != null);
        checkArgument(courseRecordRepository != null);
        checkArgument(identityService != null);
        checkArgument(registryService != null);
        this.learningCatalogueService = learningCatalogueService;
        this.courseRecordRepository = courseRecordRepository;
        this.identityService = identityService;
        this.registryService = registryService;
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
                String key = String.format("%s-%s", courseRecord.getUserId(), moduleRecord.getModuleId());

                LearnerRecordEvents eventSummary = events.computeIfAbsent(key, s -> {

                    Course course = learningCatalogueService.getCourse(courseRecord.getCourseId());
                    if (course == null) {
                        LOGGER.warn("Course not found for courseId {}.", courseRecord.getCourseId());
                        return null;
                    }

                    Module module = course.getModule(moduleRecord.getModuleId());
                    if (module == null) {
                        LOGGER.warn("Module not found for courseId {}, moduleId {}.", courseRecord.getCourseId(),
                                moduleRecord.getModuleId());
                        return null;
                    }

                    Event event = module.getEvent(moduleRecord.getEventId());
                    if (event == null) {
                        LOGGER.warn("Event not found for courseId {}, moduleId {}, eventId {}.", courseRecord.getCourseId(),
                                moduleRecord.getModuleId(), moduleRecord.getEventId());
                        return null;
                    }

                    if (event.getDate().isAfter(LocalDateTime.now())) {
                        LOGGER.debug("Event date is after today, ignoring.");
                        return null;
                    }

                    Optional<CivilServant> civilServant = registryService.getCivilServantByUid(courseRecord.getUserId());

                    if (!civilServant.isPresent()) {
                        LOGGER.warn("Civil servant not found for uid {}.", courseRecord.getUserId());
                        return null;
                    }

                    String emailAddress = identityService.getEmailAddress(courseRecord.getUserId());

                    LearnerRecordEvents newEvent = new LearnerRecordEvents();
                    newEvent.setBookingReference(String.format("REF-%s", StringUtils.leftPad(moduleRecord.getId().toString(), 6, '0')));
                    newEvent.setCourseName(course.getTitle());
                    newEvent.setCourseId(course.getId());
                    newEvent.setModuleId(module.getId());
                    newEvent.setModuleName(module.getTitle());
                    newEvent.setCost(module.getPrice());
                    newEvent.setDate(event.getDate());
                    newEvent.setDelegateEmailAddress(emailAddress);
                    newEvent.setDelegateName(civilServant.get().getFullName());
                    return newEvent;
                });

                if (eventSummary != null) {
                    eventSummary.setCreatedAt(moduleRecord.getCreatedAt());
                    eventSummary.setUpdatedAt(moduleRecord.getUpdatedAt());
                    eventSummary.setPaymentMethod(moduleRecord.getPaymentMethod());
                    eventSummary.setPaymentDetails(moduleRecord.getPaymentDetails());
                    eventSummary.setStatus(moduleRecord.getBookingStatus());
                }
            }
        }
        return new ResponseEntity<>(events.values(), OK);
    }
}