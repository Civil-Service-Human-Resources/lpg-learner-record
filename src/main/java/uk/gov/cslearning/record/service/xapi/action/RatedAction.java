package uk.gov.cslearning.record.service.xapi.action;

import gov.adlnet.xapi.model.Statement;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.service.xapi.ActivityType;
import uk.gov.cslearning.record.service.xapi.Verb;

public class RatedAction extends Action {

    static {
        for (ActivityType type : ActivityType.values()) {
            Action.register(RatedAction.class, type, Verb.RATED);
        }
    }

    RatedAction(Statement statement) {
        super(statement);
    }

    @Override
    public void replay(CourseRecord courseRecord, ModuleRecord moduleRecord) {
        if (moduleRecord != null) {
            moduleRecord.setRated(true);
        }
    }
}
