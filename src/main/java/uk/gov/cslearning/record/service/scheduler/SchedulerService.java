package uk.gov.cslearning.record.service.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.cslearning.record.domain.CompletedLearning;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.dto.CivilServantDto;
import uk.gov.cslearning.record.dto.IdentityDTO;
import uk.gov.cslearning.record.service.CompletedLearningService;
import uk.gov.cslearning.record.service.CourseRecordService;
import uk.gov.cslearning.record.service.CourseService;
import uk.gov.cslearning.record.service.UserRecordService;
import uk.gov.cslearning.record.service.catalogue.Course;
import uk.gov.cslearning.record.service.catalogue.LearningCatalogueService;
import uk.gov.cslearning.record.service.identity.CustomHttpService;

import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Controller
@RequestMapping("/test")
public class SchedulerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerService.class);

    private CustomHttpService customHttpService;
    private LearningCatalogueService learningCatalogueService;
    private UserRecordService userRecordService;
    private CourseRecordService courseRecordService;
    private CourseService courseService;
    private ScheduledNotificationsService scheduledNotificationsService;
    private CompletedLearningService completedLearningService;


    public SchedulerService(CustomHttpService customHttpService, LearningCatalogueService learningCatalogueService, UserRecordService userRecordService, CourseRecordService courseRecordService, CourseService courseService, ScheduledNotificationsService scheduledNotificationsService, CompletedLearningService completedLearningService) {
        this.customHttpService = customHttpService;
        this.learningCatalogueService = learningCatalogueService;
        this.userRecordService = userRecordService;
        this.courseRecordService = courseRecordService;
        this.courseService = courseService;
        this.scheduledNotificationsService = scheduledNotificationsService;
        this.completedLearningService = completedLearningService;
    }

    @GetMapping("/completed")
    public ResponseEntity sendLineManagerNotificationForCompletedLearning() {
        LOGGER.info("sendLineManagerNotificationForCompletedLearning");
        Map<String, IdentityDTO> identitiesMap = customHttpService.getIdentitiesMap();
        LOGGER.info("Got identities map");
        Map<String, CivilServantDto> civilServantMap = customHttpService.getCivilServantMap();
        LOGGER.info("Got csrs map");
        Map<String, List<Course>> organisationalUnitRequiredLearningMap = customHttpService.getOrganisationalUnitRequiredLearning();
        LOGGER.info("Got req learning map");

        AtomicInteger count = new AtomicInteger();
        civilServantMap.forEach((uid, civilServantDto) -> {
            if (uid.equals("8c9aba18-b351-461a-a117-e650d07bbc5c")) {
                System.out.println();
            }
            count.getAndIncrement();
            LOGGER.info("{}: Getting user {}", count, civilServantDto.toString());
            Collection<CourseRecord> courseRecords = getCourseRecordsForRequiredCourses(organisationalUnitRequiredLearningMap, uid, civilServantDto);
            LOGGER.info("User has {} course records", courseRecords.size());

            courseRecords.forEach(courseRecord -> {
                if (courseRecord.isComplete()) {
                    CompletedLearning completedLearning = new CompletedLearning(courseRecord, courseRecord.getCompletionDate().toInstant(ZoneOffset.UTC));
                    completedLearningService.save(completedLearning);
                }
            });
        });
        LOGGER.info("Complete");
        return ResponseEntity.ok("ok");
    }


    @GetMapping("/incomplete")
    public ResponseEntity sendReminderNotificationForIncompleteCourses() {
        LOGGER.info("sendReminderNotificationForIncompleteCourses");
        Map<String, IdentityDTO> identitiesMap = customHttpService.getIdentitiesMap();
        LOGGER.info("Got identities map");
        Map<String, CivilServantDto> civilServantMap = customHttpService.getCivilServantMap();
        LOGGER.info("Got csrs map");
        Map<String, List<Course>> organisationalUnitRequiredLearningMap = customHttpService.getOrganisationalUnitRequiredLearning();
        LOGGER.info("Got req learning map");

        return ResponseEntity.ok("ok");
    }

    protected Collection<CourseRecord> getCourseRecordsForRequiredCourses(Map<String, List<Course>> organisationalUnitRequiredLearningMap, String uid, CivilServantDto civilServantDto) {
        List<Course> requiredCourses = organisationalUnitRequiredLearningMap.get(civilServantDto.getOrganisation());
        List<String> requiredCoursesIds = emptyIfNull(requiredCourses)
                .stream()
                .map(Course::getId)
                .collect(Collectors.toList());

        return userRecordService.getStoredUserRecord(uid, requiredCoursesIds);
    }
}
