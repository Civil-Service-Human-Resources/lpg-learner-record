package uk.gov.cslearning.record.service.xapi.activity;

import gov.adlnet.xapi.model.Statement;
import uk.gov.cslearning.record.service.xapi.ActivityType;

public class Event extends Activity {

    private static final String COURSE_ID = "courseId";

    private static final String MODULE_ID = "moduleId";

    static {
        Activity.register(Event.class, ActivityType.COURSE);
    }

    Event(Statement statement) {
        super(statement);
    }

    @Override
    public String getCourseId() {
        return getExtensionValue(COURSE_ID);
    }

    @Override
    public String getModuleId() {
        return getExtensionValue(MODULE_ID);
    }

    @Override
    public String getEventId() {
        return getActivityId();
    }
}
