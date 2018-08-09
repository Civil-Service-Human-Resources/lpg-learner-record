package uk.gov.cslearning.record.service.xapi.action;

import gov.adlnet.xapi.model.Statement;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.service.xapi.ActivityType;
import uk.gov.cslearning.record.service.xapi.Verb;

public class LikedAction extends Action {

    static {
        for (ActivityType type : ActivityType.values()) {
            Action.register(LikedAction.class, type, Verb.LIKED);
        }
    }

    LikedAction(Statement statement) {
        super(statement);
    }

    @Override
    public void replay(CourseRecord courseRecord, ModuleRecord moduleRecord) {
        courseRecord.setState(null);
        courseRecord.setPreference("LIKED");
    }
}
