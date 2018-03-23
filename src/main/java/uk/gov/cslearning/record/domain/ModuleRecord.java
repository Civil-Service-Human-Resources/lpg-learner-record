package uk.gov.cslearning.record.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;

import static com.google.gson.internal.$Gson$Preconditions.checkArgument;

public class ModuleRecord {

    private String moduleId;

    private String eventId;

    private State state;

    private Result result;

    private String score;

    private Boolean rated = Boolean.FALSE;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime completionDate;

    public ModuleRecord(String moduleId) {
        checkArgument(moduleId != null);
        this.moduleId = moduleId;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public LocalDateTime getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDateTime completionDate) {
        this.completionDate = completionDate;
    }

    public Boolean getRated() {
        return rated;
    }

    public void setRated(Boolean rated) {
        this.rated = rated;
    }
}
