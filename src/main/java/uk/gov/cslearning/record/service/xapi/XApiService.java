package uk.gov.cslearning.record.service.xapi;

import gov.adlnet.xapi.client.StatementClient;
import gov.adlnet.xapi.model.Account;
import gov.adlnet.xapi.model.Agent;
import gov.adlnet.xapi.model.Statement;
import gov.adlnet.xapi.model.StatementResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.config.XApiProperties;

import java.io.IOException;
import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;

@Service
public class XApiService implements Serializable {

    public static final DateTimeFormatter DATE_FORMATTER = new DateTimeFormatterBuilder()
            .append(ISO_LOCAL_DATE_TIME)
            .appendLiteral('Z')
            .toFormatter();

    private static final String HOMEPAGE = "https://cslearning.gov.uk/";

    private static final Logger LOGGER = LoggerFactory.getLogger(XApiService.class);

    private XApiProperties xApiProperties;

    @Autowired
    public XApiService(XApiProperties xApiProperties) {
        checkArgument(xApiProperties != null);
        this.xApiProperties = xApiProperties;
    }

    public Collection<Statement> getStatements(String userId, String activityId) throws IOException {
        LOGGER.debug("Getting xAPI statements for user {} and activity {}", userId, activityId);

        StatementClient statementClient = new StatementClient(xApiProperties.getUrl(), xApiProperties.getUsername(),
                xApiProperties.getPassword());

        Agent agent = new Agent(null, new Account(userId, HOMEPAGE));

        statementClient = statementClient
                .filterByActor(agent);

        if (activityId != null) {
            statementClient = statementClient
                    .filterByActivity(activityId)
                    .includeRelatedActivities(true);
        }

        StatementResult result = statementClient.getStatements();

        List<Statement> statements = new ArrayList<>(result.getStatements());
        while (result.hasMore()) {
            result = statementClient.getStatements(result.getMore());
            statements.addAll(result.getStatements());
        }
        return statements;
    }

    public Collection<Statement> getStatements(String activityId) throws IOException {
        LOGGER.debug("Getting xAPI statements for activity {}", activityId);

        StatementClient statementClient = new StatementClient(xApiProperties.getUrl(), xApiProperties.getUsername(),
                xApiProperties.getPassword());

        StatementResult result = statementClient
                .filterByActivity(activityId)
                .includeRelatedActivities(true)
                .getStatements();

        List<Statement> statements = new ArrayList<>(result.getStatements());
        while (result.hasMore()) {
            result = statementClient.getStatements(result.getMore());
            statements.addAll(result.getStatements());
        }
        return statements;
    }
}
