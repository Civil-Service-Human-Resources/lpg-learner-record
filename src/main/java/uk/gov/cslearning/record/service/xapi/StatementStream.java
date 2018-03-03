package uk.gov.cslearning.record.service.xapi;

import gov.adlnet.xapi.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.cslearning.record.domain.Record;
import uk.gov.cslearning.record.service.xapi.action.Action;

import java.util.Comparator;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

public class StatementStream {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatementStream.class);

    private List<Statement> statements;

    public StatementStream(List<Statement> statements) {
        checkArgument(statements != null);
        this.statements = statements;
        this.statements.sort(Comparator.comparing(Statement::getTimestamp));
    }

    public Record replay(Record record) {
        for (Statement statement : statements) {
            Action action = Action.getFor(statement);
            if (action != null) {
                action.replay(record);
            } else {
                LOGGER.debug("Unrecognised statement {}", statement.getVerb().getId());
            }
        }
        return record;
    }
}
