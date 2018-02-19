package uk.gov.cslearning.record.service.xapi;

public enum Verb {

    COMPLETED("http://adlnet.gov/expapi/verbs/completed"),
    FAILED("http://adlnet.gov/expapi/verbs/failed"),
    INITIALISED("http://adlnet.gov/expapi/verbs/initialized"),
    LAUNCHED("http://adlnet.gov/expapi/verbs/launched"),
    PASSED("http://adlnet.gov/expapi/verbs/passed"),
    TERMINATED("http://adlnet.gov/expapi/verbs/terminated"),
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
}
