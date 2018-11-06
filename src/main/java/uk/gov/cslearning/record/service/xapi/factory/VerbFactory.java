package uk.gov.cslearning.record.service.xapi.factory;

import gov.adlnet.xapi.model.Verb;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class VerbFactory {
    public Verb createdRegistered() {
        return createVerb("registered","http://adlnet.gov/expapi/verbs/registered");
    }

    public Verb createdUnregistered() {
        return createVerb("unregistered","http://id.tincanapi.com/verb/unregistered");
    }

    private Verb createVerb(String name, String id) {
        Verb verb = new Verb();
        HashMap<String, String> display = new HashMap<>();
        display.put("en", name);
        verb.setId(id);
        verb.setDisplay(display);
        return verb;
    }
}
