package uk.gov.cslearning.record.service.xapi.action;

import gov.adlnet.xapi.model.Statement;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.domain.State;
import uk.gov.cslearning.record.service.xapi.ActivityType;
import uk.gov.cslearning.record.service.xapi.Verb;

public class SkippedAction extends Action {

    static {
        Action.register(SkippedAction.class, ActivityType.EVENT, Verb.SKIPPED);
    }

    SkippedAction(Statement statement) {
        super(statement);
    }

    @Override
    public void replay(CourseRecord courseRecord, ModuleRecord moduleRecord) {
        if (courseRecord.getState() == State.REGISTERED) {
            courseRecord.setState(State.SKIPPED);
        }
        moduleRecord.setState(State.SKIPPED);
        moduleRecord.setBookingStatus(null);
        moduleRecord.setResult(null);
        moduleRecord.setScore(null);
        moduleRecord.setCompletionDate(null);
    }
}
