package uk.gov.cslearning.record.service.xapi.action;

import gov.adlnet.xapi.model.Statement;
import uk.gov.cslearning.record.domain.CourseRecord;
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
    public void replay(CourseRecord courseRecord, ModuleRecord moduleRecord) {
        if (courseRecord.getState() == null || courseRecord.getState() != State.IN_PROGRESS) {
            courseRecord.setState(State.UNREGISTERED);
        }
        moduleRecord.setState(State.UNREGISTERED);
        moduleRecord.setResult(null);
        moduleRecord.setScore(null);
        moduleRecord.setCompletionDate(null);
    }
}
