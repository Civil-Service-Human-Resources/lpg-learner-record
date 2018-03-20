package uk.gov.cslearning.record.service.xapi.action;

import gov.adlnet.xapi.model.Statement;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.service.xapi.ActivityType;
import uk.gov.cslearning.record.service.xapi.Verb;

public class LikedAction extends Action {

    static {
        Action.register(LikedAction.class, ActivityType.FACETOFACE, Verb.LIKED);
        Action.register(LikedAction.class, ActivityType.ELEARNING, Verb.LIKED);
        Action.register(LikedAction.class, ActivityType.VIDEO, Verb.LIKED);
        Action.register(LikedAction.class, ActivityType.LINK, Verb.LIKED);
        Action.register(LikedAction.class, ActivityType.COURSE, Verb.LIKED);
        Action.register(LikedAction.class, ActivityType.EVENT, Verb.LIKED);
    }

    LikedAction(Statement statement) {
        super(statement);
    }

    @Override
    public void replay(CourseRecord courseRecord, ModuleRecord moduleRecord) {
        courseRecord.setPreference("LIKED");
    }
}
