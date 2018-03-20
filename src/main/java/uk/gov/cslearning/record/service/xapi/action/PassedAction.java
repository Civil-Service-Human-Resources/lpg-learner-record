package uk.gov.cslearning.record.service.xapi.action;

import gov.adlnet.xapi.model.Statement;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.domain.Result;
import uk.gov.cslearning.record.service.xapi.ActivityType;
import uk.gov.cslearning.record.service.xapi.Verb;

public class PassedAction extends Action {

    static {
        Action.register(PassedAction.class, ActivityType.ELEARNING, Verb.PASSED);
    }

    PassedAction(Statement statement) {
        super(statement);
    }

    @Override
    public ModuleRecord replay(ModuleRecord record) {
        record.setResult(Result.PASSED);
        return record;
    }
}
