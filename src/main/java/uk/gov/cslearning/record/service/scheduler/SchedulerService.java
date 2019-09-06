package uk.gov.cslearning.record.service.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.domain.CompletedLearningEvent;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.scheduler.LineManagerRequiredLearningNotificationEvent;
import uk.gov.cslearning.record.domain.scheduler.RequiredLearningDueNotificationEvent;
import uk.gov.cslearning.record.dto.CivilServantDto;
import uk.gov.cslearning.record.dto.IdentityDto;
import uk.gov.cslearning.record.service.CompletedLearningEventService;
import uk.gov.cslearning.record.service.UserRecordService;
import uk.gov.cslearning.record.service.catalogue.Course;
import uk.gov.cslearning.record.service.http.CustomHttpService;
import uk.gov.cslearning.record.service.scheduler.events.LineManagerRequiredLearningNotificationEventService;
import uk.gov.cslearning.record.service.scheduler.events.RequiredLearningDueNotificationEventService;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

@Service
public class SchedulerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerService.class);

    private static final String DAY_PERIOD = "1 day";

    private static final String WEEK_PERIOD = "1 week";

    private static final String MONTH_PERIOD = "1 month";

    private CustomHttpService customHttpService;
    private UserRecordService userRecordService;
    private CompletedLearningEventService completedLearningService;
    private RequiredLearningDueNotificationEventService requiredLearningDueNotificationEventService;
    private LineManagerRequiredLearningNotificationEventService lineManagerRequiredLearningNotificationEventService;

    public SchedulerService(CustomHttpService customHttpService, UserRecordService userRecordService, CompletedLearningEventService completedLearningService, RequiredLearningDueNotificationEventService requiredLearningDueNotificationEventService, LineManagerRequiredLearningNotificationEventService lineManagerRequiredLearningNotificationEventService) {
        this.customHttpService = customHttpService;
        this.userRecordService = userRecordService;
        this.completedLearningService = completedLearningService;
        this.requiredLearningDueNotificationEventService = requiredLearningDueNotificationEventService;
        this.lineManagerRequiredLearningNotificationEventService = lineManagerRequiredLearningNotificationEventService;
    }

    public void processReminderNotificationForIncompleteLearning() {
        Map<String, IdentityDto> identitiesMap = customHttpService.getIdentitiesMap();
        LOGGER.info("Got {} identities", identitiesMap.size());

        Map<String, Map<String, List<Course>>> requiredLearningDue = new HashMap<>();

        requiredLearningDue.put(DAY_PERIOD, customHttpService.getRequiredLearningDueWithinPeriod(0, 1));
        requiredLearningDue.put(WEEK_PERIOD, customHttpService.getRequiredLearningDueWithinPeriod(1, 7));
        requiredLearningDue.put(MONTH_PERIOD, customHttpService.getRequiredLearningDueWithinPeriod(7, 30));

        LOGGER.info("Got {} requiredLearningDue for {}", requiredLearningDue.get(DAY_PERIOD).size(), DAY_PERIOD);
        LOGGER.info("Got {} requiredLearningDue for {}", requiredLearningDue.get(WEEK_PERIOD).size(), WEEK_PERIOD);
        LOGGER.info("Got {} requiredLearningDue for {}", requiredLearningDue.get(MONTH_PERIOD).size(), MONTH_PERIOD);

        requiredLearningDue.forEach((periodText, organisationalUnitRequiredLearningMap) -> {
            organisationalUnitRequiredLearningMap.forEach((organisationalUnitCode, courses) -> {
                LOGGER.info("{}: Got {} and {} courses", periodText, organisationalUnitCode, courses.size());

                Map<String, CivilServantDto> civilServantMap = customHttpService.getCivilServantMapByOrganisation(organisationalUnitCode);
                LOGGER.info("{}: Got {} civil servants", periodText, civilServantMap.size());

                civilServantMap.forEach((s, civilServantDto) -> {
                    LOGGER.info("{}: Processing {} ", periodText, civilServantDto.toString());
                    List<String> courseIds = emptyIfNull(courses)
                            .stream()
                            .map(Course::getId)
                            .collect(Collectors.toList());

                    Collection<CourseRecord> storedUserRecords = userRecordService.getStoredUserRecord(civilServantDto.getUid(), courseIds);
                    courses.forEach(course -> {
                        if (!course.isComplete(storedUserRecords)) {
                            IdentityDto identityDto = identitiesMap.get(civilServantDto.getUid());

                            if (identityDto == null) {
                                return;
                            }

                            RequiredLearningDueNotificationEvent requiredLearningDueNotificationEvent = new RequiredLearningDueNotificationEvent(identityDto.getUsername(), identityDto.getUid(), course.getId(), course.getTitle(), periodText, Instant.now());
                            if (requiredLearningDueNotificationEventService.doesExist(requiredLearningDueNotificationEvent)) {
                                LOGGER.info("{}: Required learning already tracked for {}", periodText, requiredLearningDueNotificationEvent.toString());
                            } else {
                                LOGGER.info("{}: Saving required learning due for {}", periodText, requiredLearningDueNotificationEvent.toString());
                                requiredLearningDueNotificationEventService.save(requiredLearningDueNotificationEvent);
                            }
                        }
                    });
                });
            });
        });
        LOGGER.info("Complete");
    }

    public void processLineManagerNotificationForCompletedLearning() {
        LOGGER.info("processLineManagerNotificationForCompletedLearning");

        Map<String, IdentityDto> identitiesMap = customHttpService.getIdentitiesMap();
        Map<String, List<Course>> organisationalUnitRequiredLearningMap = customHttpService.getOrganisationalUnitRequiredLearning();
        Map<String, CivilServantDto> civilServantMap = customHttpService.getCivilServantsMap();

        List<CompletedLearningEvent> completedLearningList = completedLearningService.findAll();
        LOGGER.info("Completed learning count: {}", completedLearningList.size());

        completedLearningList.forEach(completedLearning -> {
            LOGGER.info("Getting completed learning {}", completedLearning.toString());
            CourseRecord courseRecord = completedLearning.getCourseRecord();
            CivilServantDto civilServantDto = civilServantMap.get(courseRecord.getUserId());

            if (civilServantDto == null) {
                return;
            }

            List<Course> requiredCoursesForOrg = organisationalUnitRequiredLearningMap.get(civilServantDto.getOrganisation());
            boolean isCompletedLearningRequired = emptyIfNull(requiredCoursesForOrg)
                    .stream()
                    .anyMatch(course -> course.getId().equals(courseRecord.getCourseId()));

            try {
                if (isCompletedLearningRequired) {
                    IdentityDto lineManagerIdentityDto = identitiesMap.get(civilServantDto.getLineManagerUid());

                    LineManagerRequiredLearningNotificationEvent lineManagerRequiredLearningNotificationEvent = new LineManagerRequiredLearningNotificationEvent(lineManagerIdentityDto.getUsername(), civilServantDto.getName(), civilServantDto.getUid(), courseRecord.getCourseId(), courseRecord.getCourseTitle(), Instant.now());
                    lineManagerRequiredLearningNotificationEventService.save(lineManagerRequiredLearningNotificationEvent);
                } else {
                    LOGGER.info("Completed learning is not required");
                }
                LOGGER.info("Removing {}", completedLearning.toString());

                completedLearningService.delete(completedLearning);
            } catch (Exception e) {
                LOGGER.error("Could not send notification for completedLearning {}", completedLearning.toString());
            }
        });

        LOGGER.info("Process line manager notifications complete");
    }

    public void sendLineManagerNotificationForCompletedLearningRetroactive() {
        LOGGER.info("sendLineManagerNotificationForCompletedLearning");

        Map<String, CivilServantDto> civilServantMap = customHttpService.getCivilServantsByOrganisationalUnitCodeMap();
        LOGGER.info("Got csrs map");

        Map<String, List<Course>> organisationalUnitRequiredLearningMap = customHttpService.getOrganisationalUnitRequiredLearning();
        LOGGER.info("Got req learning map");

        civilServantMap.forEach((uid, civilServantDto) -> {
            List<Course> requiredCourses = organisationalUnitRequiredLearningMap.get(civilServantDto.getOrganisation());
            List<String> requiredCoursesIds = emptyIfNull(requiredCourses)
                    .stream()
                    .map(Course::getId)
                    .collect(Collectors.toList());

            Collection<CourseRecord> storedUserRecord = userRecordService.getStoredUserRecord(uid, requiredCoursesIds);
            LOGGER.info("User has {} course records", storedUserRecord.size());

            storedUserRecord.forEach(courseRecord -> {
                if (courseRecord.isComplete()) {
                    CompletedLearningEvent completedLearning = new CompletedLearningEvent(courseRecord, courseRecord.getCompletionDate().toInstant(ZoneOffset.UTC));
                    completedLearningService.save(completedLearning);
                }
            });
        });
        LOGGER.info("Complete");
    }
}
