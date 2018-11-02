package uk.gov.cslearning.record.service.xapi.factory;

import gov.adlnet.xapi.model.Activity;
import gov.adlnet.xapi.model.IStatementObject;
import org.junit.Test;

import static org.junit.Assert.*;

public class IStatementObjectFactoryTest {
    private IStatementObjectFactory factory = new IStatementObjectFactory();

    @Test
    public void shouldReturnEventObject() {
        String eventId = "event-id";

        Activity object = (Activity)factory.createEventObject(eventId);

        assertEquals("Activity", object.getObjectType());
        assertEquals("http://adlnet.gov/expapi/activities/event", object.getDefinition().getType());
        assertEquals("Face to Face Module", object.getDefinition().getName().get("en"));
        assertEquals(eventId, object.getId());
    }
}