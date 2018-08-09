package uk.gov.cslearning.record.service.xapi.action;

import gov.adlnet.xapi.model.Statement;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.domain.Result;
import uk.gov.cslearning.record.domain.State;
import uk.gov.cslearning.record.service.xapi.ActivityType;
import uk.gov.cslearning.record.service.xapi.Verb;

public class FailedAction extends Action {

    static {
        Action.register(FailedAction.class, ActivityType.ELEARNING, Verb.FAILED);
    }

    FailedAction(Statement statement) {
        super(statement);
    }

    @Override
    public void replay(CourseRecord courseRecord, ModuleRecord moduleRecord) {
        moduleRecord.setResult(Result.FAILED);
    }
}
