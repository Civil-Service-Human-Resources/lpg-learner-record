package uk.gov.cslearning.record.service.xapi;

public enum ActivityType {

    COURSE("http://adlnet.gov/expapi/activities/course"),
    ELEARNING("http://cslearning.gov.uk/activities/elearning"),
    EVENT("http://adlnet.gov/expapi/activities/event"),
    FACETOFACE("http://cslearning.gov.uk/activities/face-to-face"),
    FILE("http://adlnet.gov/expapi/activities/file"),
    LINK("http://adlnet.gov/expapi/activities/link"),
    VIDEO("https://w3id.org/xapi/acrossx/activities/video");

    public static ActivityType fromUri(String uri) {
        for (ActivityType type : values()) {
            if (type.uri.equals(uri)) {
                return type;
            }
        }
        return null;
    }

    private String uri;

    ActivityType(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }
}
