package uk.gov.cslearning.record.service.xapi.activity;

import gov.adlnet.xapi.model.ActivityDefinition;
import gov.adlnet.xapi.model.ContextActivities;
import gov.adlnet.xapi.model.Statement;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.cslearning.record.service.xapi.ActivityType;

import java.util.*;

import static java.util.Collections.emptyList;

public abstract class Activity {

    public static final String COURSE_ID_PREFIX = "http://cslearning.gov.uk/courses";

    public static final String MODULE_ID_PREFIX = "http://cslearning.gov.uk/modules";

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
        ActivityType type = null;
        if (definition != null) {
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

    String getActivityId() {
        gov.adlnet.xapi.model.Activity activity = ((gov.adlnet.xapi.model.Activity) statement.getObject());
        if (activity == null) {
            LOGGER.warn("Statement has no activity ID", statement);
            return null;
        }
        return activity.getId();
    }

    String getParent(String idPrefix) {
        List<gov.adlnet.xapi.model.Activity> parents = null;

        if (statement.getContext() != null) {
            ContextActivities activities = statement.getContext().getContextActivities();
            if (activities != null) {
                parents = activities.getParent();
            }
        }

        if (parents == null) {
            parents = emptyList();
        }

        Optional<gov.adlnet.xapi.model.Activity> parent = parents.stream()
                .filter(activity -> activity.getId().startsWith(idPrefix))
                .findFirst();

        return parent
                .map(gov.adlnet.xapi.model.Activity::getId)
                .orElseGet(() -> {
                    LOGGER.debug("No parent found in statement {} for prefix {}", statement.getId(), idPrefix);
                    return null;
                });
    }

    public abstract String getCourseId();

    public abstract String getModuleId();

    public abstract String getEventId();
}
