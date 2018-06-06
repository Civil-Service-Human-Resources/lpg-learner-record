package uk.gov.cslearning.record.service;

import gov.adlnet.xapi.model.Activity;
import gov.adlnet.xapi.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.repository.CourseRecordRepository;
import uk.gov.cslearning.record.service.xapi.StatementStream;
import uk.gov.cslearning.record.service.xapi.XApiService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;

@Service
public class UserRecordService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRecordService.class);

    private CourseRecordRepository courseRecordRepository;

    private XApiService xApiService;

    private RegistryService registryService;

    @Autowired
    public UserRecordService(CourseRecordRepository courseRecordRepository, XApiService xApiService, RegistryService registryService) {
        checkArgument(courseRecordRepository != null);
        checkArgument(xApiService != null);
        checkArgument(registryService != null);
        this.courseRecordRepository = courseRecordRepository;
        this.xApiService = xApiService;
        this.registryService = registryService;
    }

    @Transactional
    public Collection<CourseRecord> getUserRecord(String userId, String activityId) {
        LOGGER.debug("Retrieving user record for user {}, activity {} and state {}", userId, activityId);

        Collection<CourseRecord> existingCourseRecords;

        if (activityId != null) {
            existingCourseRecords = courseRecordRepository.findByUserIdAndCourseId(userId, activityId);
        } else {
            existingCourseRecords = courseRecordRepository.findByUserId(userId);
        }

        LocalDateTime since = existingCourseRecords.stream()
                .map(CourseRecord::getLastUpdated)
                .filter(Objects::nonNull)
                .reduce((a, b) -> a.isAfter(b) ? a : b)
                .orElse(null);

        try {
            Collection<Statement> statements = xApiService.getStatements(userId, activityId, since);

            StatementStream stream = new StatementStream();
            Collection<CourseRecord> latestCourseRecords = stream.replay(statements, statement -> ((Activity) statement.getObject()).getId(), existingCourseRecords);

            setUserDepartmentAndProfession(userId, latestCourseRecords);

            courseRecordRepository.saveAll(latestCourseRecords);
            return latestCourseRecords;
        } catch (IOException e) {
            throw new RuntimeException("Exception retrieving xAPI statements.", e);
        }
    }

    private void setUserDepartmentAndProfession(String userId, Collection<CourseRecord> courseRecords) {
        if (!courseRecords.isEmpty()) {
            LOGGER.debug("Updating course records with additional user information.");
            CivilServant civilServant = registryService.getCivilServantByUid(userId);
            for (CourseRecord courseRecord : courseRecords) {
                courseRecord.setDepartment(civilServant.getDepartmentCode());
                courseRecord.setProfession(civilServant.getProfession());
            }
        }
    }
}
