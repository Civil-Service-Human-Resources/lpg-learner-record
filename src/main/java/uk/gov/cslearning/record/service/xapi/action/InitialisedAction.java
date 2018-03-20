package uk.gov.cslearning.record.service.xapi.action;

import gov.adlnet.xapi.model.Statement;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;
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
    public void replay(CourseRecord courseRecord, ModuleRecord moduleRecord) {
        courseRecord.setState(State.IN_PROGRESS);
        moduleRecord.setState(State.IN_PROGRESS);
        moduleRecord.setResult(null);
        moduleRecord.setScore(null);
        moduleRecord.setCompletionDate(null);
    }
}
