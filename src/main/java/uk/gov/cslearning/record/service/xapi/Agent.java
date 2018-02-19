package uk.gov.cslearning.record.service.xapi;

public class Agent {

    private String name;

    private String objectType = "Agent";

    private String mbox = "mailto:noone@cslearning.gov.uk";

    public Agent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getObjectType() {
        return objectType;
    }
}
