package uk.gov.cslearning.record.service.xapi.factory;

import gov.adlnet.xapi.model.Account;
import gov.adlnet.xapi.model.Actor;
import gov.adlnet.xapi.model.Agent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ActorFactory {

    private final String homepage;

    public ActorFactory(@Value("${csl.homepage}") String homepage) {
        this.homepage = homepage;
    }

    public Actor create(String userId) {
        return new Agent(null, new Account(userId, homepage));
    }
}
