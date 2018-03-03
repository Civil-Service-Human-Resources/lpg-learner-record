package uk.gov.cslearning.record.service.xapi.activity;

import gov.adlnet.xapi.model.Statement;
import uk.gov.cslearning.record.service.xapi.ActivityType;

public class Course extends Activity {

    static {
        Activity.register(Course.class, ActivityType.COURSE);
    }

    Course(Statement statement) {
        super(statement);
    }

    @Override
    public String getCourseId() {
        return getActivityId();
    }

    @Override
    public String getModuleId() {
        return null;
    }

    @Override
    public String getEventId() {
        return null;
    }
}
