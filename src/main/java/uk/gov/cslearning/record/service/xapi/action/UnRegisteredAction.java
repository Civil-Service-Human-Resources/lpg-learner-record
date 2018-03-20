package uk.gov.cslearning.record.service.xapi.action;

import gov.adlnet.xapi.model.Statement;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.domain.State;
import uk.gov.cslearning.record.service.xapi.ActivityType;
import uk.gov.cslearning.record.service.xapi.Verb;

public class UnRegisteredAction extends Action {

    static {
        Action.register(UnRegisteredAction.class, ActivityType.EVENT, Verb.UNREGISTERED);
    }

    UnRegisteredAction(Statement statement) {
        super(statement);
    }

    @Override
    public ModuleRecord replay(ModuleRecord record) {
        record.setState(State.UNREGISTERED);
        record.setResult(null);
        record.setScore(null);
        record.setCompletionDate(null);
        return record;
    }
}
