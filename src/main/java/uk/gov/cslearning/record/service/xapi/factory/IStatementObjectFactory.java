package uk.gov.cslearning.record.service.xapi.factory;

import com.google.common.collect.ImmutableMap;
import gov.adlnet.xapi.model.Activity;
import gov.adlnet.xapi.model.ActivityDefinition;
import gov.adlnet.xapi.model.IStatementObject;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class IStatementObjectFactory {
    public IStatementObject createEventObject(String eventId) {
        ActivityDefinition definition = new ActivityDefinition();
        definition.setName(new HashMap<>(ImmutableMap.of("en", "Face to Face Module")));
        definition.setType("http://adlnet.gov/expapi/activities/event");

        Activity activity = new Activity();
        activity.setDefinition(definition);
        activity.setId(eventId);

        return activity;
    }
}
