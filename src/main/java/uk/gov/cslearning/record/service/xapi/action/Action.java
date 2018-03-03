package uk.gov.cslearning.record.service.xapi.action;

import gov.adlnet.xapi.model.Activity;
import gov.adlnet.xapi.model.Statement;
import uk.gov.cslearning.record.domain.Record;
import uk.gov.cslearning.record.service.xapi.ActivityType;
import uk.gov.cslearning.record.service.xapi.Verb;

import java.util.HashMap;
import java.util.Map;

public abstract class Action {

    private static final Map<String, Class<? extends Action>> ACTIONS = new HashMap<>();

    public static Action getFor(Statement statement) {
        ActivityType type = ActivityType.fromUri(((Activity) statement.getObject()).getDefinition().getType());
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
        ACTIONS.put(type.name() + verb.name(), actionClass);
    }

    protected Statement statement;

    Action(Statement statement) {
        this.statement = statement;
    }

    public abstract Record replay(Record record);
}
