package uk.gov.cslearning.record.service.xapi.activity;

import gov.adlnet.xapi.model.Statement;
import uk.gov.cslearning.record.service.xapi.ActivityType;

public class Event extends Activity {

    static {
        Activity.register(Event.class, ActivityType.EVENT);
    }

    Event(Statement statement) {
        super(statement);
    }

    @Override
    public String getCourseId() {
        return getParent(COURSE_ID_PREFIX);
    }

    @Override
    public String getModuleId() {
        return getParent(MODULE_ID_PREFIX);
    }

    @Override
    public String getEventId() {
        return getActivityId();
    }
}
