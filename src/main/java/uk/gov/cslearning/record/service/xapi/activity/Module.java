package uk.gov.cslearning.record.service.xapi.activity;

import gov.adlnet.xapi.model.Statement;
import uk.gov.cslearning.record.service.xapi.ActivityType;

public class Module extends Activity {

    static {
        Activity.register(Module.class, ActivityType.FACETOFACE);
        Activity.register(Module.class, ActivityType.LINK);
        Activity.register(Module.class, ActivityType.FILE);
        Activity.register(Module.class, ActivityType.ELEARNING);
        Activity.register(Module.class, ActivityType.VIDEO);
    }

    Module(Statement statement) {
        super(statement);
    }

    @Override
    public String getCourseId() {
        return getParent(COURSE_ID_PREFIX);
    }

    @Override
    public String getModuleId() {
        return getActivityId();
    }

    @Override
    public String getEventId() {
        return null;
    }
}
