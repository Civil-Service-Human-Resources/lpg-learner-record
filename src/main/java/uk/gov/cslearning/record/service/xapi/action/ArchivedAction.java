package uk.gov.cslearning.record.service.xapi.action;

import gov.adlnet.xapi.model.Statement;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.domain.State;
import uk.gov.cslearning.record.service.xapi.ActivityType;
import uk.gov.cslearning.record.service.xapi.Verb;

public class ArchivedAction extends Action {

    static {
        for (ActivityType type : ActivityType.values()) {
            Action.register(ArchivedAction.class, type, Verb.ARCHIVED);
        }
        Action.register(ArchivedAction.class, ActivityType.FACETOFACE, Verb.UNREGISTERED);
    }

    ArchivedAction(Statement statement) {
        super(statement);
    }

    @Override
    public void replay(CourseRecord courseRecord, ModuleRecord moduleRecord) {
        courseRecord.setState(State.ARCHIVED);
    }
}
