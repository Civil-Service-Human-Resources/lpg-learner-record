package uk.gov.cslearning.record.service.xapi.factory;

import gov.adlnet.xapi.model.Verb;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class VerbFactory {
    public Verb createdRegistered() {
        Verb verb = new Verb();
        HashMap<String, String> display = new HashMap<>();
        display.put("en", "registered");
        verb.setId("http://adlnet.gov/expapi/verbs/registered");
        verb.setDisplay(display);
        return verb;
    }
}
