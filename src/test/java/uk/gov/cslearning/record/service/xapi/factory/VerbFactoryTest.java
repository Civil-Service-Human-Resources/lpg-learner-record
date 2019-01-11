package uk.gov.cslearning.record.service.xapi.factory;

import gov.adlnet.xapi.model.Verb;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class VerbFactoryTest {
    private final VerbFactory verbFactory = new VerbFactory();

    @Test
    public void shouldReturnRegisteredVerb() {
        Verb verb = verbFactory.createdRegistered();

        assertEquals("http://adlnet.gov/expapi/verbs/registered", verb.getId());
        assertEquals("registered", verb.getDisplay().get("en"));
    }

    @Test
    public void shouldReturnUnregisteredVerb() {
        Verb verb = verbFactory.createdUnregistered();

        assertEquals("http://id.tincanapi.com/verb/unregistered", verb.getId());
        assertEquals("unregistered", verb.getDisplay().get("en"));
    }

}