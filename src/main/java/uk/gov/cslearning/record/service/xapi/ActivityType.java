package uk.gov.cslearning.record.service.xapi;

public enum ActivityType {

    FACETOFACE("http://cslearning.gov.uk/types/face-to-face"),
    COURSE("http://adlnet.gov/expapi/activities/course"),
    ELEARNING("http://cslearning.gov.uk/types/elearning"),
    EVENT("http://activitystrea.ms/schema/1.0/event"),
    VIDEO("https://w3id.org/xapi/acrossx/activities/video"),
    LINK("http://adlnet.gov/expapi/activities/link");

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
