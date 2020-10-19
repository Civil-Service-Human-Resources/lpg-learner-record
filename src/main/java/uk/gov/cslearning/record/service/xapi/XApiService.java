package uk.gov.cslearning.record.service.xapi;

import gov.adlnet.xapi.client.StatementClient;
import gov.adlnet.xapi.model.Account;
import gov.adlnet.xapi.model.Agent;
import gov.adlnet.xapi.model.Statement;
import gov.adlnet.xapi.model.StatementResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.config.XApiProperties;
import uk.gov.cslearning.record.dto.BookingDto;
import uk.gov.cslearning.record.service.xapi.exception.XApiException;
import uk.gov.cslearning.record.service.xapi.factory.StatementClientFactory;
import uk.gov.cslearning.record.service.xapi.factory.StatementFactory;
import uk.gov.cslearning.record.service.xapi.factory.VerbFactory;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;

@Service
public class XApiService implements Serializable {
    public static final DateTimeFormatter DATE_FORMATTER = new DateTimeFormatterBuilder()
            .append(ISO_LOCAL_DATE_TIME)
            .appendLiteral('Z')
            .toFormatter();
    private static final Logger LOGGER = LoggerFactory.getLogger(XApiService.class);
    private static final String HOMEPAGE = "https://cslearning.gov.uk/";

    private final XApiProperties xApiProperties;
    private final StatementClientFactory statementClientFactory;
    private final StatementFactory statementFactory;

    public XApiService(XApiProperties xApiProperties, StatementClientFactory statementClientFactory, StatementFactory statementFactory) {
        this.xApiProperties = xApiProperties;
        this.statementClientFactory = statementClientFactory;
        this.statementFactory = statementFactory;
    }
    public Collection<Statement> getStatements(String userId, String activityId, LocalDateTime since) throws IOException {
        return getStatements(userId, activityId, null, since);
    }

    public Collection<Statement> getStatements(String userId, String activityId,  String verb, LocalDateTime since) throws IOException {
        LOGGER.debug("Getting xAPI statements for user {} and activity {} since {}", userId, activityId, since);

        StatementClient statementClient = new StatementClient(xApiProperties.getUrl(), xApiProperties.getUsername(),
                xApiProperties.getPassword());

        if (userId != null) {
            Agent agent = new Agent(null, new Account(userId, HOMEPAGE));
            statementClient = statementClient
                .filterByActor(agent);
        }

        if (activityId != null) {
            statementClient = statementClient
                    .filterByActivity(activityId)
                    .includeRelatedActivities(true);
        }

        if (verb != null) {
            statementClient = statementClient.filterByVerb(VerbFactory.createCompleted());
        }

        if (since != null) {
            statementClient = statementClient
                    .filterBySince(DATE_FORMATTER.format(since));
        }

        StatementResult result = statementClient.getStatements();

        List<Statement> statements = new ArrayList<>(result.getStatements());
        while (result.hasMore()) {
            result = statementClient.getStatements(stripPath(xApiProperties.getUrl(), result.getMore()));
            statements.addAll(result.getStatements());
        }
        return statements;
    }


    private String stripPath(String url, String more) {
        int length = 1;
        while (!url.endsWith(more.substring(0, length))) {
            length += 1;
        }
        return more.substring(length);
    }

    public String register(BookingDto bookingDto) {
        return postStatement(statementFactory.createRegisteredStatement(bookingDto));
    }

    public String approve(BookingDto bookingDto) {
        return postStatement(statementFactory.createApprovedStatement(bookingDto));
    }

    public String unregister(BookingDto bookingDto) {
        return postStatement(statementFactory.createUnregisteredStatement(bookingDto));
    }

    private String postStatement(Statement statement) {
        StatementClient statementClient = statementClientFactory.create();
        try {
            return statementClient.postStatement(statement);
        } catch (IOException e) {
            throw new XApiException("Unable to post statement to XApi", e);
        }
    }
}
