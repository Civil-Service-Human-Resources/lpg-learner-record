package uk.gov.cslearning.record.service.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.domain.CompletedLearning;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.dto.CivilServantDto;
import uk.gov.cslearning.record.dto.IdentityDTO;
import uk.gov.cslearning.record.service.CompletedLearningService;
import uk.gov.cslearning.record.service.UserRecordService;
import uk.gov.cslearning.record.service.catalogue.Course;
import uk.gov.cslearning.record.service.identity.CustomHttpService;

import java.time.ZoneOffset;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Service
public class SchedulerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerService.class);

    private static final String DAY_PERIOD = "1 day";

    private static final String WEEK_PERIOD = "1 week";

    private static final String MONTH_PERIOD = "1 month";

    private CustomHttpService customHttpService;
    private UserRecordService userRecordService;
    private CompletedLearningService completedLearningService;
    private ScheduledNotificationsService scheduledNotificationsService;

    public SchedulerService(CustomHttpService customHttpService,
                            UserRecordService userRecordService,
                            ScheduledNotificationsService scheduledNotificationsService,
                            CompletedLearningService completedLearningService) {
        this.customHttpService = customHttpService;
        this.userRecordService = userRecordService;
        this.scheduledNotificationsService = scheduledNotificationsService;
        this.completedLearningService = completedLearningService;
    }

    public void sendLineManagerNotificationForCompletedLearning() {
        LOGGER.info("sendLineManagerNotificationForCompletedLearning");

        Map<String, CivilServantDto> civilServantMap = customHttpService.getCivilServantMap();
        LOGGER.info("Got csrs map");

        Map<String, List<Course>> organisationalUnitRequiredLearningMap = customHttpService.getOrganisationalUnitRequiredLearning();
        LOGGER.info("Got req learning map");

        civilServantMap.forEach((uid, civilServantDto) -> {
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
    }

    public void sendReminderNotificationForIncompleteLearning() {
        Map<String, IdentityDTO> identitiesMap = customHttpService.getIdentitiesMap();

        Map<String, Map<String, List<Course>>> requiredLearningDue = new HashMap<>();

        requiredLearningDue.put(DAY_PERIOD, customHttpService.getRequiredLearningDueWithinPeriod(1));
        requiredLearningDue.put(WEEK_PERIOD, customHttpService.getRequiredLearningDueWithinPeriod(7));
        requiredLearningDue.put(MONTH_PERIOD, customHttpService.getRequiredLearningDueWithinPeriod(30));

        requiredLearningDue.forEach((periodText, organisationalUnitRequiredLearningMap) -> {
            organisationalUnitRequiredLearningMap.forEach((organisationalUnitCode, courses) -> {
                LOGGER.info("Got {} and {} courses", organisationalUnitCode, courses.size());

                Map<String, CivilServantDto> civilServantMap = customHttpService.getCivilServantMapByOrganisation(organisationalUnitCode);
                LOGGER.info("Got {} civil servants", civilServantMap.size());

                civilServantMap.forEach((s, civilServantDto) -> {
                    LOGGER.info("Processing {} ", civilServantDto.toString());
                    List<String> courseIds = emptyIfNull(courses)
                            .stream()
                            .map(Course::getId)
                            .collect(Collectors.toList());

                    Collection<CourseRecord> storedUserRecords = userRecordService.getStoredUserRecord(civilServantDto.getUid(), courseIds);
                    courses.forEach(course -> {
                        if (!course.isComplete(storedUserRecords)) {
                            IdentityDTO identityDTO = identitiesMap.get(civilServantDto.getUid());
                            scheduledNotificationsService.sendRequiredLearningDueNotification(identityDTO, course, periodText);
                        }
                    });

                });
            });
        });
        LOGGER.info("complete");
    }

    public void sendLineManagerNotificationForCompletedLearningRetroactive() {
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
                    if (scheduledNotificationsService.shouldSendLineManagerNotification(courseRecord.getUserId(), courseRecord.getCourseId(), courseRecord.getCompletionDate())) {
                        IdentityDTO lineManagerIdentityDto = identitiesMap.get(civilServantDto.getLineManagerUid());
                        scheduledNotificationsService.sendLineManagerNotification(lineManagerIdentityDto.getUsername(), civilServantDto.getName(), courseRecord.getUserId(), courseRecord);
                        LOGGER.info("Sending notification for user {} and course {}", civilServantDto.getName(), courseRecord.getCourseTitle());
                    } else {
                        LOGGER.info("User {} has already been sent notification for course {}", courseRecord.getUserId(), courseRecord.getCourseTitle());
                    }
                } else {
                    LOGGER.info("Completed learning is not required");
                }
                LOGGER.info("Removing {}", completedLearning.toString());

                completedLearningService.delete(completedLearning);
            } catch (Exception e) {
                LOGGER.error("Could not send notification for completedLearning {}", completedLearning.toString());
            }
        });

        LOGGER.info("Sending line manager notifications complete");
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
