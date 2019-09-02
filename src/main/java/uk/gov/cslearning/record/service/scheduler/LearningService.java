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
import uk.gov.cslearning.record.service.catalogue.Course;
import uk.gov.cslearning.record.service.identity.CustomHttpService;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/test2")
public class LearningService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LearningService.class);

    private CustomHttpService customHttpService;
    private ScheduledNotificationsService scheduledNotificationsService;
    private CompletedLearningService completedLearningService;

    public LearningService(CustomHttpService customHttpService,
                           ScheduledNotificationsService scheduledNotificationsService,
                           CompletedLearningService completedLearningService) {
        this.customHttpService = customHttpService;
        this.scheduledNotificationsService = scheduledNotificationsService;
        this.completedLearningService = completedLearningService;
    }

    @GetMapping
    public ResponseEntity sendLineManagerNotificationForCompletedLearning() {
        LOGGER.info("sendLineManagerNotificationForCompletedLearning");

        Map<String, IdentityDTO> identitiesMap = customHttpService.getIdentitiesMap();
        Map<String, List<Course>> organisationalUnitRequiredLearningMap = customHttpService.getOrganisationalUnitRequiredLearning();
        Map<String, CivilServantDto> civilServantMap = customHttpService.getCivilServantMap();

        List<CompletedLearning> completedLearningList = completedLearningService.findAll();
        LOGGER.info("Completed learning count: {}", completedLearningList.size());

        completedLearningList.forEach(completedLearning -> {
            LOGGER.info("Getting completed learning {}", completedLearning.toString());
            CourseRecord courseRecord = completedLearning.getCourseRecord();
            CivilServantDto civilServantDto = civilServantMap.get(courseRecord.getUserId());

            List<Course> requiredCoursesForOrg = organisationalUnitRequiredLearningMap.get(civilServantDto.getOrganisation());
            boolean isCompletedLearningRequired = requiredCoursesForOrg
                    .stream()
                    .anyMatch(course -> course.getId().equals(courseRecord.getCourseId()));

            try {
                if (isCompletedLearningRequired) {
                    if (scheduledNotificationsService.shoudSendNotification(courseRecord.getUserId(), courseRecord.getCourseId(), courseRecord.getCompletionDate())) {
                        IdentityDTO lineManagerIdentityDto = identitiesMap.get(civilServantDto.getLineManagerUid());
                        scheduledNotificationsService.sendNotification(lineManagerIdentityDto.getUsername(), civilServantDto.getName(), courseRecord.getUserId(), courseRecord);
                        LOGGER.info("Sending notification for user {} and course {}", civilServantDto.getName(), courseRecord.getCourseTitle());
                    } else {
                        LOGGER.info("User {} has already been sent notification for course {}", courseRecord.getUserId(), courseRecord.getCourseTitle());
                    }
                } else {
                    LOGGER.info("Completed learning is not required");
                }
                completedLearningService.delete(completedLearning);
            } catch (Exception e) {
                LOGGER.error("Could not send notification for completedLearning {}", completedLearning.toString());
            }
        });

        LOGGER.info("Sending line manager notifications complete");
        return ResponseEntity.ok("ok");
    }
}
