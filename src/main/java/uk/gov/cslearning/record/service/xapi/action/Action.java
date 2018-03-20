package uk.gov.cslearning.record.service.xapi.action;

import gov.adlnet.xapi.model.ActivityDefinition;
import gov.adlnet.xapi.model.Statement;
import org.reflections.Reflections;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.service.xapi.ActivityType;
import uk.gov.cslearning.record.service.xapi.Verb;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class Action {

    private static final Map<String, Class<? extends Action>> ACTIONS = new HashMap<>();

    static {
        Reflections reflections = new Reflections(Action.class.getPackage().getName());
        Set<Class<? extends Action>> classes = reflections.getSubTypesOf(Action.class);
        for (Class<? extends Action> clazz : classes) {
            try {
                clazz.getDeclaredConstructor(Statement.class).newInstance(null);
            } catch (Exception e) {
            }
        }
    }

    public static Action getFor(Statement statement) {
        ActivityDefinition definition = ((gov.adlnet.xapi.model.Activity) statement.getObject()).getDefinition();
        ActivityType type = null;
        if (definition != null) {
            type = ActivityType.fromUri(definition.getType());
        }
        Verb verb = Verb.fromUri(statement.getVerb().getId());
        if (type != null && verb != null) {
            Class<? extends Action> actionClass = ACTIONS.get(type.name() + verb.name());
            if (actionClass != null) {
                try {
                    return actionClass.getDeclaredConstructor(Statement.class).newInstance(statement);
                } catch (Exception e) {
                    throw new RuntimeException("Exception instantiating action class. Does it have a no args constructor?", e);
                }
            }
        }
        return null;
    }

    protected static void register(Class<? extends Action> actionClass, ActivityType type, Verb verb) {
        String key = type.name() + verb.name();
        if (ACTIONS.containsKey(key)) {
            throw new RuntimeException("ACTIONS already contains mapping for " + key);
        }
        ACTIONS.put(key, actionClass);
    }

    protected Statement statement;

    Action(Statement statement) {
        this.statement = statement;
    }

    public abstract void replay(CourseRecord courseRecord, ModuleRecord record);
}
