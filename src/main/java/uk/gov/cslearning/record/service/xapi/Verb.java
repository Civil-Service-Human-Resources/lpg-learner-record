package uk.gov.cslearning.record.service.xapi;

public enum Verb {

    ARCHIVED("https://w3id.org/xapi/dod-isd/verbs/archived"),
    COMPLETED("http://adlnet.gov/expapi/verbs/completed"),
    DISLIKED("https://w3id.org/xapi/acrossx/verbs/disliked"),
    EXPERIENCED("http://adlnet.gov/expapi/verbs/experienced"),
    FAILED("http://adlnet.gov/expapi/verbs/failed"),
    INITIALISED("http://adlnet.gov/expapi/verbs/initialized"),
    LAUNCHED("http://adlnet.gov/expapi/verbs/launched"),
    LIKED("https://w3id.org/xapi/acrossx/verbs/liked"),
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
