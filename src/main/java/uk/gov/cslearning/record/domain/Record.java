package uk.gov.cslearning.record.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

public class Record {

    private String activityId;

    private String state;

    private String result;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime completionDate;

    @JsonCreator
    public Record(@JsonProperty("activityId") String activityId, @JsonProperty("state") String state,
                  @JsonProperty("result") String result, @JsonProperty("completionDate") LocalDateTime completionDate) {
        this.activityId = activityId;
        this.state = state;
        this.result = result;
        this.completionDate = completionDate;
    }

    public String getActivityId() {
        return activityId;
    }

    public String getState() {
        return state;
    }

    public LocalDateTime getCompletionDate() {
        return completionDate;
    }

    public String getResult() {
        return result;
    }
}
