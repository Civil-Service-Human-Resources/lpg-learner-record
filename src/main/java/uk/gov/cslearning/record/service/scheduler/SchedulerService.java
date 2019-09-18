package uk.gov.cslearning.record.service.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.scheduler.RequiredLearningDueNotificationEvent;
import uk.gov.cslearning.record.dto.CivilServantDto;
import uk.gov.cslearning.record.dto.IdentityDto;
import uk.gov.cslearning.record.service.UserRecordService;
import uk.gov.cslearning.record.service.catalogue.Course;
import uk.gov.cslearning.record.service.http.CustomHttpService;
import uk.gov.cslearning.record.service.scheduler.events.RequiredLearningDueNotificationEventService;

import java.time.Instant;
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
    private RequiredLearningDueNotificationEventService requiredLearningDueNotificationEventService;

    public SchedulerService(CustomHttpService customHttpService, UserRecordService userRecordService, RequiredLearningDueNotificationEventService requiredLearningDueNotificationEventService) {
        this.customHttpService = customHttpService;
        this.userRecordService = userRecordService;
        this.requiredLearningDueNotificationEventService = requiredLearningDueNotificationEventService;
    }

    @Transactional
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

                civilServantMap.forEach((uid, civilServantDto) -> {
                    LOGGER.info("{}: Processing {} ", periodText, civilServantDto.toString());
                    List<String> courseIds = emptyIfNull(courses)
                            .stream()
                            .map(Course::getId)
                            .collect(Collectors.toList());

                    Collection<CourseRecord> storedUserRecords = userRecordService.getStoredUserRecord(civilServantDto.getUid(), courseIds);
                    courses.forEach(course -> {
                        if (!course.isComplete(storedUserRecords)) {
                            if (!identitiesMap.containsKey(uid)) {
                                LOGGER.info("Civil servant {} does not have an identity", uid);
                                return;
                            }
                            IdentityDto identityDto = identitiesMap.get(uid);

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
}
