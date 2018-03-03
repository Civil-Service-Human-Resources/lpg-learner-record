package uk.gov.cslearning.record.service.xapi.action;

import gov.adlnet.xapi.model.Statement;
import uk.gov.cslearning.record.domain.Record;
import uk.gov.cslearning.record.domain.State;
import uk.gov.cslearning.record.service.xapi.ActivityType;
import uk.gov.cslearning.record.service.xapi.Verb;

public class InitialisedAction extends Action {

    static {
        Action.register(InitialisedAction.class, ActivityType.ELEARNING, Verb.INITIALISED);
        Action.register(InitialisedAction.class, ActivityType.ELEARNING, Verb.LAUNCHED);
        Action.register(InitialisedAction.class, ActivityType.VIDEO, Verb.INITIALISED);
    }

    InitialisedAction(Statement statement) {
        super(statement);
    }

    @Override
    public Record replay(Record record) {
        record.setState(State.IN_PROGRESS);
        record.setResult(null);
        record.setScore(null);
        record.setCompletionDate(null);
        return record;
    }
}
