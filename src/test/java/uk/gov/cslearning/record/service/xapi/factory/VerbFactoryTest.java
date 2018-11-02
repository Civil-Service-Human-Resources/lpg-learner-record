package uk.gov.cslearning.record.service.xapi.factory;

import gov.adlnet.xapi.model.Verb;
import org.junit.Test;

import static org.junit.Assert.*;

public class VerbFactoryTest {
    private final VerbFactory verbFactory = new VerbFactory();

    @Test
    public void shouldReturnRegisteredVerb() {
        Verb verb = verbFactory.createdRegistered();

        assertEquals("http://adlnet.gov/expapi/verbs/registered", verb.getId());
        assertEquals("registered", verb.getDisplay().get("en"));
    }
}