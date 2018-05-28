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
import java.util.stream.StreamSupport;

import static com.google.common.base.Preconditions.checkArgument;

@Service
public class UserRecordService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRecordService.class);

    private CourseRecordRepository courseRecordRepository;

    private XApiService xApiService;

    @Autowired
    public UserRecordService(CourseRecordRepository courseRecordRepository, XApiService xApiService) {
        checkArgument(courseRecordRepository != null);
        checkArgument(xApiService != null);
        this.courseRecordRepository = courseRecordRepository;
        this.xApiService = xApiService;
    }

    @Transactional
    public Collection<CourseRecord> getUserRecord(String userId, String activityId) {
        LOGGER.debug("Retrieving user record for user {}, activity {} and state {}", userId, activityId);

        Collection<CourseRecord> existingCourseRecords = courseRecordRepository.findByUserId(userId);
        LocalDateTime since = existingCourseRecords.stream()
                .map(CourseRecord::getLastUpdated)
                .reduce((a, b) -> a.isAfter(b) ? a : b)
                .orElse(null);

        try {
            Collection<Statement> statements = xApiService.getStatements(userId, activityId, since);

            StatementStream stream = new StatementStream();
            Collection<CourseRecord> latestCourseRecords = stream.replay(statements, statement -> ((Activity) statement.getObject()).getId(), existingCourseRecords);
            courseRecordRepository.saveAll(latestCourseRecords);
            return latestCourseRecords;
        } catch (IOException e) {
            throw new RuntimeException("Exception retrieving xAPI statements.", e);
        }
    }

    @Transactional(readOnly = true)
    public Iterable<CourseRecord> listAllRecords() {
        LOGGER.debug("Retrieving all records");
        return courseRecordRepository.findAll();
    }
}
