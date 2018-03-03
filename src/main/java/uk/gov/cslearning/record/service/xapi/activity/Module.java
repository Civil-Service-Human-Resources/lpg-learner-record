package uk.gov.cslearning.record.service.xapi.activity;

import gov.adlnet.xapi.model.Statement;
import uk.gov.cslearning.record.service.xapi.ActivityType;

public class Module extends Activity {

    private static final String COURSE_ID = "courseId";

    static {
        Activity.register(Module.class, ActivityType.COURSE);
    }

    Module(Statement statement) {
        super(statement);
    }

    @Override
    public String getCourseId() {
        return getExtensionValue(COURSE_ID);
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
