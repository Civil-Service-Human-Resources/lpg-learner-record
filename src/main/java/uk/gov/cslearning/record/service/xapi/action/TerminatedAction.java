package uk.gov.cslearning.record.service.xapi.action;

import gov.adlnet.xapi.model.Statement;
import uk.gov.cslearning.record.domain.Record;
import uk.gov.cslearning.record.domain.State;
import uk.gov.cslearning.record.service.xapi.ActivityType;
import uk.gov.cslearning.record.service.xapi.Verb;

public class TerminatedAction extends Action {

    static {
        Action.register(TerminatedAction.class, ActivityType.FACETOFACE, Verb.TERMINATED);
        Action.register(TerminatedAction.class, ActivityType.FACETOFACE, Verb.UNREGISTERED);
        Action.register(TerminatedAction.class, ActivityType.ELEARNING, Verb.TERMINATED);
        Action.register(TerminatedAction.class, ActivityType.LINK, Verb.TERMINATED);
        Action.register(TerminatedAction.class, ActivityType.VIDEO, Verb.TERMINATED);
    }

    TerminatedAction(Statement statement) {
        super(statement);
    }

    @Override
    public Record replay(Record record) {
        record.setState(State.TERMINATED);
        record.setResult(null);
        record.setScore(null);
        record.setCompletionDate(null);
        return record;
    }
}
