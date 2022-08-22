package uk.gov.cslearning.record.service;

import gov.adlnet.xapi.model.Activity;
import gov.adlnet.xapi.model.Statement;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cslearning.record.csrs.service.RegistryService;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.repository.CourseRecordRepository;
import uk.gov.cslearning.record.service.catalogue.LearningCatalogueService;

import java.io.IOException;
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
    private LearningCatalogueService learningCatalogueService;
    private RegistryService registryService;

    @Autowired
    public UserRecordService(CourseRecordRepository courseRecordRepository,
                             LearningCatalogueService learningCatalogueService,
                             CollectionsService collectionsService,
                             RegistryService registryService) {
        checkArgument(courseRecordRepository != null);
        checkArgument(learningCatalogueService != null);
        this.courseRecordRepository = courseRecordRepository;
        this.learningCatalogueService = learningCatalogueService;
        this.collectionsService = collectionsService;
        this.registryService = registryService;
    }

    public Collection<CourseRecord> getUserRecord(String userId) {
        LOGGER.debug("Retrieving user record for user {}", userId);
        return courseRecordRepository.findByUserId(userId);
    }

    public Collection<CourseRecord> getUserRecordWithActivities(String userId, List<String> activityIds) {
        Collection<CourseRecord> courseRecords = courseRecordRepository.findByUserId(userId);
        if (activityIds != null && !activityIds.isEmpty()) {
            return courseRecords.stream()
                    .filter(courseRecord -> activityIds.stream().anyMatch(courseRecord::matchesActivityId))
                    .collect(Collectors.toSet());
        }
        return courseRecords;
    }

    public Collection<CourseRecord> getUserRecord(String userId, List<String> activityIds) {
        LOGGER.debug("Retrieving user record for user {}, activities {}", userId, activityIds);

        Collection<CourseRecord> courseRecords = courseRecordRepository.findByUserId(userId);

        LocalDateTime since = courseRecords.stream()
                .map(CourseRecord::getLastUpdated)
                .filter(Objects::nonNull)
                .reduce((a, b) -> a.isAfter(b) ? a : b)
                .orElse(null);

        if (activityIds != null && !activityIds.isEmpty()) {
            return courseRecords.stream()
                    .filter(courseRecord -> activityIds.stream().anyMatch(courseRecord::matchesActivityId))
                    .collect(Collectors.toSet());
        }

        return courseRecords;
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
}
