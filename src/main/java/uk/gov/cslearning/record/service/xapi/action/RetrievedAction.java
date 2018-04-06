package uk.gov.cslearning.record.service.xapi.action;

import gov.adlnet.xapi.model.Statement;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.domain.State;
import uk.gov.cslearning.record.service.xapi.ActivityType;
import uk.gov.cslearning.record.service.xapi.Verb;

public class RetrievedAction extends Action {

    static {
        Action.register(RetrievedAction.class, ActivityType.COURSE, Verb.RETRIEVED);
        Action.register(RetrievedAction.class, ActivityType.FACETOFACE, Verb.RETRIEVED);
        Action.register(RetrievedAction.class, ActivityType.ELEARNING, Verb.RETRIEVED);
        Action.register(RetrievedAction.class, ActivityType.LINK, Verb.RETRIEVED);
        Action.register(RetrievedAction.class, ActivityType.VIDEO, Verb.RETRIEVED);
    }

    RetrievedAction(Statement statement) {
        super(statement);
    }

    @Override
    public void replay(CourseRecord courseRecord, ModuleRecord moduleRecord) {
        courseRecord.setState(State.RETRIEVED);
    }
}
