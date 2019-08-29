package uk.gov.cslearning.record.service.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.dto.CivilServantDto;
import uk.gov.cslearning.record.dto.IdentityDTO;
import uk.gov.cslearning.record.service.CourseRecordService;
import uk.gov.cslearning.record.service.CourseService;
import uk.gov.cslearning.record.service.UserRecordService;
import uk.gov.cslearning.record.service.catalogue.Course;
import uk.gov.cslearning.record.service.catalogue.LearningCatalogueService;
import uk.gov.cslearning.record.service.identity.CustomHttpService;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/test")
public class CompletedLearningService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompletedLearningService.class);

    private CustomHttpService customHttpService;
    private LearningCatalogueService learningCatalogueService;
    private UserRecordService userRecordService;
    private CourseRecordService courseRecordService;
    private CourseService courseService;
    private ScheduledNotificationsService scheduledNotificationsService;

    public CompletedLearningService(CustomHttpService customHttpService, LearningCatalogueService learningCatalogueService, UserRecordService userRecordService, CourseRecordService courseRecordService, CourseService courseService, ScheduledNotificationsService scheduledNotificationsService) {
        this.customHttpService = customHttpService;
        this.learningCatalogueService = learningCatalogueService;
        this.userRecordService = userRecordService;
        this.courseRecordService = courseRecordService;
        this.courseService = courseService;
        this.scheduledNotificationsService = scheduledNotificationsService;
    }

    @GetMapping
    public ResponseEntity sendLineManagerNotificationForCompletedLearning() {
        Map<String, IdentityDTO> identitiesMap = customHttpService.getIdentitiesMap();
        Map<String, CivilServantDto> civilServantMap = customHttpService.getCivilServantMap();
        Map<String, List<Course>> organisationalUnitRequiredLearningMap = customHttpService.getOrganisationalUnitRequiredLearning();

        CivilServantDto civilServantDto = civilServantMap.get("8c9aba18-b351-461a-a117-e650d07bbc5c");

        List<Course> requiredCourses = organisationalUnitRequiredLearningMap.get(civilServantDto.getOrganisation());
        List<String> requiredCoursesIds = requiredCourses
                .stream()
                .map(Course::getId)
                .collect(Collectors.toList());

        Collection<CourseRecord> courseRecords = userRecordService.getStoredUserRecord(civilServantDto.getUid(), requiredCoursesIds);

        courseRecords.forEach(courseRecord -> {
            if (courseRecord.isComplete()) {
                if (scheduledNotificationsService.hasNotificationBeenSentBefore(civilServantDto.getUid(), courseRecord.getCourseId(), courseRecord.getCompletionDate())) {
                    LOGGER.info("User has already been sent notification");
                } else {
                    IdentityDTO lineManagerIdentityDto = identitiesMap.get(civilServantDto.getLineManagerUid());

                    scheduledNotificationsService.sendNotification(lineManagerIdentityDto.getUsername(), civilServantDto.getName(), civilServantDto.getUid(), courseRecord);

                    System.out.println();
                }
            }
        });
        return ResponseEntity.ok("ok");
    }
}
