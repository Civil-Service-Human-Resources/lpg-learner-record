package uk.gov.cslearning.record.service.xapi.activity;

import com.google.gson.JsonElement;
import gov.adlnet.xapi.model.ActivityDefinition;
import gov.adlnet.xapi.model.Statement;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.cslearning.record.service.xapi.ActivityType;
import uk.gov.cslearning.record.service.xapi.action.Action;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class Activity {

    private static final Logger LOGGER = LoggerFactory.getLogger(Activity.class);

    private static final Map<ActivityType, Class<? extends Activity>> ACTIVITIES = new HashMap<>();

    static {
        Reflections reflections = new Reflections(Activity.class.getPackage().getName());
        Set<Class<? extends Activity>> classes = reflections.getSubTypesOf(Activity.class);
        for (Class<? extends Activity> clazz : classes) {
            try {
                clazz.getDeclaredConstructor(Statement.class).newInstance(null);
            } catch (Exception e) {
            }
        }
    }

    protected Statement statement;

    Activity(Statement statement) {
        this.statement = statement;
    }

    public static Activity getFor(Statement statement) {
        ActivityDefinition definition = ((gov.adlnet.xapi.model.Activity) statement.getObject()).getDefinition();
        ActivityType type;
        if (definition == null) {
            type = ActivityType.COURSE;
        } else {
            type = ActivityType.fromUri(definition.getType());
        }
        Class<? extends Activity> activityClass = ACTIVITIES.get(type);
        if (activityClass != null) {
            try {
                return activityClass.getDeclaredConstructor(Statement.class).newInstance(statement);
            } catch (Exception e) {
                throw new RuntimeException("Exception instantiating activity class. Does it have a no args constructor?", e);
            }
        }
        return null;
    }

    protected static void register(Class<? extends Activity> activityClass, ActivityType type) {
        if (ACTIVITIES.containsKey(type)) {
            throw new RuntimeException("Activities already contains mapping for " + type);
        }
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
