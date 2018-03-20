package uk.gov.cslearning.record.service.xapi.action;

import gov.adlnet.xapi.model.Statement;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.domain.State;
import uk.gov.cslearning.record.service.xapi.ActivityType;
import uk.gov.cslearning.record.service.xapi.Verb;

public class RegisteredAction extends Action {

    static {
        Action.register(RegisteredAction.class, ActivityType.EVENT, Verb.REGISTERED);
    }

    RegisteredAction(Statement statement) {
        super(statement);
    }

    @Override
    public ModuleRecord replay(ModuleRecord record) {
        record.setState(State.REGISTERED);
        record.setResult(null);
        record.setScore(null);
        record.setCompletionDate(null);
        return record;
    }
}
