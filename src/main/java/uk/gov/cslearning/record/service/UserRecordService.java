package uk.gov.cslearning.record.service;

import gov.adlnet.xapi.model.Activity;
import gov.adlnet.xapi.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.service.xapi.StatementStream;
import uk.gov.cslearning.record.service.xapi.XApiService;

import java.io.IOException;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkArgument;

@Service
public class UserRecordService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRecordService.class);

    private XApiService xApiService;

    @Autowired
    public UserRecordService(XApiService xApiService) {
        checkArgument(xApiService != null);
        this.xApiService = xApiService;
    }

    public Collection<CourseRecord> getUserRecord(String userId, String activityId) {
        LOGGER.debug("Retrieving user record for user {}, activity {} and state {}", userId, activityId);
        try {
            Collection<Statement> statements = xApiService.getStatements(userId, activityId);

            StatementStream stream = new StatementStream();
            return stream.replay(statements, statement -> ((Activity) statement.getObject()).getId());
        } catch (IOException e) {
            throw new RuntimeException("Exception retrieving xAPI statements.", e);
        }
    }
}
