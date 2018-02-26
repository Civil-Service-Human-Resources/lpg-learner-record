package uk.gov.cslearning.record.service.xapi;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Statement implements Serializable {

    private String activityId;

    private String userId;

    private Verb verb;

    private String score;

    private LocalDateTime timestamp;

    public Statement(String activityId, String userId, Verb verb, String score, LocalDateTime timestamp) {
        this.activityId = activityId;
        this.userId = userId;
        this.verb = verb;
        this.score = score;
        this.timestamp = timestamp;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Verb getVerb() {
        return verb;
    }

    public void setVerb(Verb verb) {
        this.verb = verb;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
