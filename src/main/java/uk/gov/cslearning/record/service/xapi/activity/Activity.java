package uk.gov.cslearning.record.service.xapi.activity;

import com.google.gson.JsonElement;
import gov.adlnet.xapi.model.ActivityDefinition;
import gov.adlnet.xapi.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.cslearning.record.service.xapi.ActivityType;

import java.util.HashMap;
import java.util.Map;

public abstract class Activity {

    private static final Logger LOGGER = LoggerFactory.getLogger(Activity.class);

    private static final Map<ActivityType, Class<? extends Activity>> ACTIVITIES = new HashMap<>();
    protected Statement statement;

    Activity(Statement statement) {
        this.statement = statement;
    }

    public static Activity getFor(Statement statement) {
        ActivityType type = ActivityType.fromUri(((gov.adlnet.xapi.model.Activity) statement.getObject()).getDefinition().getType());
        if (type != null) {
            Class<? extends Activity> activityClass = ACTIVITIES.get(type);
            if (activityClass != null) {
                try {
                    return activityClass.getDeclaredConstructor(Statement.class).newInstance(statement);
                } catch (Exception e) {
                    throw new RuntimeException("Exception instantiating activity class. Does it have a no args constructor?", e);
                }
            }
        }
        return null;
    }

    protected static void register(Class<? extends Activity> activityClass, ActivityType type) {
        ACTIVITIES.put(type, activityClass);
    }

    gov.adlnet.xapi.model.Activity getXApiActivity() {
        return ((gov.adlnet.xapi.model.Activity) statement.getObject());
    }

    ActivityDefinition getXApiActivityDefinition() {
        return getXApiActivity().getDefinition();
    }

    String getActivityId() {
        if (getXApiActivity() == null) {
            LOGGER.warn("Statement has no activity ID", statement);
            return null;
        }
        return getXApiActivity().getId();
    }

    String getExtensionValue(String key) {
        Map<String, JsonElement> extensions = getXApiActivityDefinition().getExtensions();
        if (extensions == null || extensions.containsKey(key)) {
            LOGGER.warn("Statement recorded with no {}", key, statement);
            return null;
        }
        return extensions.get(key).getAsString();
    }

    public abstract String getCourseId();

    public abstract String getModuleId();

    public abstract String getEventId();
}
