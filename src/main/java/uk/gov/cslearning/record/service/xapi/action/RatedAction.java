package uk.gov.cslearning.record.service.xapi.action;

import gov.adlnet.xapi.model.Statement;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.domain.State;
import uk.gov.cslearning.record.service.xapi.ActivityType;
import uk.gov.cslearning.record.service.xapi.Verb;

public class RatedAction extends Action {

    static {
        Action.register(RatedAction.class, ActivityType.LINK, Verb.RATED);
        Action.register(RatedAction.class, ActivityType.FILE, Verb.RATED);
        Action.register(RatedAction.class, ActivityType.EVENT, Verb.RATED);
        Action.register(RatedAction.class, ActivityType.VIDEO, Verb.RATED);
        Action.register(RatedAction.class, ActivityType.ELEARNING, Verb.RATED);
    }

    RatedAction(Statement statement) {
        super(statement);
    }

    @Override
    public void replay(CourseRecord courseRecord, ModuleRecord moduleRecord) {
        moduleRecord.setRated(true);
    }
}
