package uk.gov.cslearning.record.service;

import gov.adlnet.xapi.model.Account;
import gov.adlnet.xapi.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.domain.Record;
import uk.gov.cslearning.record.service.xapi.StatementStream;
import uk.gov.cslearning.record.service.xapi.XApiService;

import java.io.IOException;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkArgument;

@Service
public class ActivityRecordService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivityRecordService.class);

    private XApiService xApiService;

    @Autowired
    public ActivityRecordService(XApiService xApiService) {
        checkArgument(xApiService != null);
        this.xApiService = xApiService;
    }

    public Collection<Record> getActivityRecord(String activityId) {
        LOGGER.debug("Retrieving activity record");
        try {
            Collection<Statement> statements = xApiService.getStatements(activityId);

            StatementStream stream = new StatementStream();
            return stream.replay(statements, statement -> {
                Account account = statement.getActor().getAccount();
                if (account != null) {
                    return account.getName();
                }
                return null;
            });
        } catch (IOException e) {
            throw new RuntimeException("Exception retrieving xAPI statements.", e);
        }
    }
}
