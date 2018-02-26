package uk.gov.cslearning.record.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;

public class Registration {

    private String activityId;

    private String userId;

    private String state;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime lastUpdated;

    @JsonCreator
    public Registration(@JsonProperty("activityId") String activityId, @JsonProperty("state") String state,
                        @JsonProperty("userId ") String userId,
                        @JsonProperty("lastUpdated") LocalDateTime lastUpdated) {
        this.activityId = activityId;
        this.userId = userId;
        this.state = state;
        this.lastUpdated = lastUpdated;
    }

    public String getActivityId() {
        return activityId;
    }

    public String getUserId() {
        return userId;
    }

    public String getState() {
        return state;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
}
