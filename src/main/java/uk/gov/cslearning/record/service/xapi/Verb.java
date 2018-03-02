package uk.gov.cslearning.record.service.xapi;

public enum Verb {

    LIKED("https://w3id.org/xapi/acrossx/verbs/liked"),
    DISLIKED("https://w3id.org/xapi/acrossx/verbs/disliked"),
    COMPLETED("http://adlnet.gov/expapi/verbs/completed"),
    FAILED("http://adlnet.gov/expapi/verbs/failed"),
    INITIALISED("http://adlnet.gov/expapi/verbs/initialized"),
    LAUNCHED("http://adlnet.gov/expapi/verbs/launched"),
    PASSED("http://adlnet.gov/expapi/verbs/passed"),
    REGISTERED("http://adlnet.gov/expapi/verbs/registered"),
    TERMINATED("http://adlnet.gov/expapi/verbs/terminated"),
    UNREGISTERED("http://adlnet.gov/expapi/verbs/unregistered"),
    VIEWED("http://id.tincanapi.com/verb/viewed");

    public static Verb fromUri(String uri) {
        for (Verb verb : values()) {
            if (verb.uri.equals(uri)) {
                return verb;
            }
        }
        return null;
    }

    private String uri;

    Verb(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }
}
