package uk.gov.cslearning.record.service.xapi.action;

import gov.adlnet.xapi.model.Statement;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.service.xapi.ActivityType;
import uk.gov.cslearning.record.service.xapi.Verb;

public class DislikedAction extends Action {

    static {
        Action.register(DislikedAction.class, ActivityType.FACETOFACE, Verb.DISLIKED);
        Action.register(DislikedAction.class, ActivityType.ELEARNING, Verb.DISLIKED);
        Action.register(DislikedAction.class, ActivityType.VIDEO, Verb.DISLIKED);
        Action.register(DislikedAction.class, ActivityType.LINK, Verb.DISLIKED);
        Action.register(DislikedAction.class, ActivityType.COURSE, Verb.DISLIKED);
        Action.register(DislikedAction.class, ActivityType.EVENT, Verb.DISLIKED);
    }

    DislikedAction(Statement statement) {
        super(statement);
    }

    @Override
    public ModuleRecord replay(ModuleRecord record) {
        record.setPreference("DISLIKED");
        return record;
    }
}
