package uk.gov.cslearning.record.service;

import gov.adlnet.xapi.model.Activity;
import gov.adlnet.xapi.model.Statement;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.csrs.domain.CivilServant;
import uk.gov.cslearning.record.csrs.service.RegistryService;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.Statements;
import uk.gov.cslearning.record.repository.CourseRecordRepository;
import uk.gov.cslearning.record.repository.StatementsRepository;
import uk.gov.cslearning.record.service.catalogue.LearningCatalogueService;
import uk.gov.cslearning.record.service.xapi.StatementStream;
import uk.gov.cslearning.record.service.xapi.XApiService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

@Service
public class UserRecordService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRecordService.class);

    private CourseRecordRepository courseRecordRepository;

    private XApiService xApiService;

    private RegistryService registryService;

    private LearningCatalogueService learningCatalogueService;

    private final StatementsRepository statementsRepository;

    private final int dataRetentionTime;

    @Autowired
    public UserRecordService(@Value("${data.retentionTime}") int dataRetentionTime, CourseRecordRepository courseRecordRepository, XApiService xApiService,
                             RegistryService registryService, LearningCatalogueService learningCatalogueService, StatementsRepository statementsRepository) {
        checkArgument(courseRecordRepository != null);
        checkArgument(xApiService != null);
        checkArgument(registryService != null);
        checkArgument(learningCatalogueService != null);
        this.courseRecordRepository = courseRecordRepository;
        this.xApiService = xApiService;
        this.registryService = registryService;
        this.learningCatalogueService = learningCatalogueService;
        this.statementsRepository = statementsRepository;
        this.dataRetentionTime = dataRetentionTime;
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

            setUserDepartmentAndProfession(userId, updatedCourseRecords);

            for (CourseRecord courseRecord : updatedCourseRecords) {
                if (!courseRecords.contains(courseRecord)) {
                    courseRecords.add(courseRecord);
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

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void setUserDepartmentAndProfession(String userId, Collection<CourseRecord> courseRecords) {
        if (!courseRecords.isEmpty()) {
            LOGGER.debug("Updating course records with additional user information.");
            Optional<CivilServant> optionalCivilServant = registryService.getCivilServantByUid(userId);
            if (optionalCivilServant.isPresent()) {
                CivilServant civilServant = optionalCivilServant.get();
                for (CourseRecord courseRecord : courseRecords) {
                    courseRecord.setDepartment(civilServant.getOrganisationalUnit().getCode());
                    courseRecord.setProfession(civilServant.getProfession().getName());
                }
            }
            courseRecordRepository.saveAll(courseRecords);
        }
    }

    @Transactional
    public void deleteUserRecords(String uid) {
        statementsRepository.deleteAllByLearnerUid(uid);
        courseRecordRepository.deleteAllByUid(uid);
    }

    @Transactional
    public void deleteOldStatements() {
        DateTime dateTime = DateTime.now().minusMonths(dataRetentionTime);
        statementsRepository.deleteAllByAge(dateTime);
    }
}
