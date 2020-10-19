package uk.gov.cslearning.record.service.xapi.factory;

import gov.adlnet.xapi.model.Verb;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class VerbFactory {
    public Verb createdRegistered() {
        return createVerb("registered", "http://adlnet.gov/expapi/verbs/registered", "en");
    }

    public Verb createdUnregistered() {
        return createVerb("unregistered", "http://id.tincanapi.com/verb/unregistered", "en");
    }

    public Verb createdApproved() {
        return createVerb("approved", "http://id.tincanapi.com/verb/approved", "en");
    }

    public static Verb createCompleted() {
        return createVerb("completed", uk.gov.cslearning.record.service.xapi.Verb.COMPLETED.getUri(), "en-GB");
    }

    private static Verb createVerb(String name, String id, String displayLanguage) {
        Verb verb = new Verb();
        HashMap<String, String> display = new HashMap<>();
        display.put(displayLanguage, name);
        verb.setId(id);
        verb.setDisplay(display);
        return verb;
    }
}
