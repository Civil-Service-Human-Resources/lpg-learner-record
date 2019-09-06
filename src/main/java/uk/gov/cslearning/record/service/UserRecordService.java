package uk.gov.cslearning.record.service;

import gov.adlnet.xapi.model.Activity;
import gov.adlnet.xapi.model.Statement;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.domain.CompletedLearningEvent;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.State;
import uk.gov.cslearning.record.repository.CourseRecordRepository;
import uk.gov.cslearning.record.service.catalogue.LearningCatalogueService;
import uk.gov.cslearning.record.service.xapi.StatementStream;
import uk.gov.cslearning.record.service.xapi.XApiService;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

@Service
public class UserRecordService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRecordService.class);
    private final CollectionsService collectionsService;
    private CourseRecordRepository courseRecordRepository;
    private XApiService xApiService;
    private LearningCatalogueService learningCatalogueService;
    private CompletedLearningEventService completedLearningEventService;

    @Autowired
    public UserRecordService(CourseRecordRepository courseRecordRepository,
                             XApiService xApiService,
                             LearningCatalogueService learningCatalogueService,
                             CollectionsService collectionsService,
                             CompletedLearningEventService completedLearningEventService) {
        checkArgument(courseRecordRepository != null);
        checkArgument(xApiService != null);
        checkArgument(learningCatalogueService != null);
        this.courseRecordRepository = courseRecordRepository;
        this.xApiService = xApiService;
        this.learningCatalogueService = learningCatalogueService;
        this.collectionsService = collectionsService;
        this.completedLearningEventService = completedLearningEventService;
    }

    @Transactional
    public Collection<CourseRecord> getUserRecord(String userId, List<String> activityIds) {
        LOGGER.debug("Retrieving user record for user {}, activities {}", userId, activityIds);

        Collection<CourseRecord> courseRecords = courseRecordRepository.findByUserId(userId);

        LocalDateTime since = courseRecords.stream()
                .map(CourseRecord::getLastUpdated)
                .filter(Objects::nonNull)
                .reduce((a, b) -> a.isAfter(b) ? a : b)
                .orElse(null);

        try {
            Collection<Statement> statements = xApiService.getStatements(userId, null, since);

            StatementStream stream = new StatementStream(learningCatalogueService);

            Collection<CourseRecord> updatedCourseRecords = stream.replay(statements,
                    statement -> ((Activity) statement.getObject()).getId(),
                    courseRecords);

            courseRecordRepository.saveAll(updatedCourseRecords);

            for (CourseRecord courseRecord : updatedCourseRecords) {
                if (!courseRecords.contains(courseRecord)) {
                    courseRecords.add(courseRecord);
                    if (courseRecord.getState().equals(State.COMPLETED)) {
                        CompletedLearningEvent completedLearning = new CompletedLearningEvent(courseRecord, Instant.now());
                        completedLearningEventService.save(completedLearning);
                    }
                }
            }

            if (activityIds != null && !activityIds.isEmpty()) {
                return courseRecords.stream()
                        .filter(courseRecord -> activityIds.stream().anyMatch(courseRecord::matchesActivityId))
                        .collect(Collectors.toSet());
            }

            return courseRecords;
        } catch (IOException e) {
            throw new RuntimeException("Exception retrieving xAPI statements.", e);
        }
    }

    @Transactional
    public void deleteUserRecords(String uid) {
        collectionsService.deleteAllByLearnerUid(uid);
        courseRecordRepository.deleteAllByUid(uid);
    }

    @Transactional
    public void deleteOldRecords(DateTime dateTime, LocalDateTime localDateTime) {
        collectionsService.deleteAllByAge(dateTime);
        courseRecordRepository.deleteAllByLastUpdatedBefore(localDateTime);
    }

    /**
     * This method gets the user records without checking for xApi updates as per {@link UserRecordService#getUserRecord(String, List)},
     * allowing for much quicker read of a user's learner record.
     * It should only be used in instances where you do not expect the course records to require any update.
     */
    @Transactional
    public Collection<CourseRecord> getStoredUserRecord(String userId, List<String> activityIds) {
        Collection<CourseRecord> courseRecords = courseRecordRepository.findByUserId(userId);

        return courseRecords.stream()
                .filter(courseRecord -> activityIds.stream().anyMatch(courseRecord::matchesActivityId))
                .collect(Collectors.toSet());
    }
}
