package uk.gov.cslearning.record.service.xapi.action;

import gov.adlnet.xapi.model.Statement;
import uk.gov.cslearning.record.domain.Record;
import uk.gov.cslearning.record.domain.State;
import uk.gov.cslearning.record.service.xapi.ActivityType;
import uk.gov.cslearning.record.service.xapi.Verb;
import uk.gov.cslearning.record.service.xapi.XApiService;

import java.time.LocalDateTime;

public class CompletedAction extends Action {

    static {
        Action.register(CompletedAction.class, ActivityType.ELEARNING, Verb.COMPLETED);
        Action.register(CompletedAction.class, ActivityType.VIDEO, Verb.COMPLETED);
        Action.register(CompletedAction.class, ActivityType.LINK, Verb.EXPERIENCED);
    }

    CompletedAction(Statement statement) {
        super(statement);
    }

    @Override
    public Record replay(Record record) {
        record.setState(State.COMPLETED);
        record.setCompletionDate(LocalDateTime.parse(statement.getTimestamp(), XApiService.DATE_FORMATTER));
        return record;
    }
}
