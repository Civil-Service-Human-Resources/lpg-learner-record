package uk.gov.cslearning.record.service.xapi.factory;

import gov.adlnet.xapi.model.Actor;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ActorFactoryTest {
    private final String homepage = "home-page";
    private final ActorFactory actorFactory = new ActorFactory(homepage);

    @Test
    public void shouldReturnActor() {
        String userId = "user-id";

        Actor actor = actorFactory.create(userId);

        assertEquals(userId, actor.getAccount().getName());
        assertEquals(homepage, actor.getAccount().getHomePage());
    }
}