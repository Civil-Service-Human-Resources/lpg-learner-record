package uk.gov.cslearning.record.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static com.google.gson.internal.$Gson$Preconditions.checkArgument;

@Entity
public class CourseRecord {

    @JsonIgnore
    @EmbeddedId
    @Valid
    private CourseRecordIdentity identity;

    @NotBlank(message = "courseTitle is required")
    private String courseTitle;

    @Enumerated(EnumType.STRING)
    private State state;

    @Enumerated(EnumType.STRING)
    private Preference preference;

    @JsonIgnore
    private String profession;

    @JsonIgnore
    private String department;

    @JsonIgnore
    private boolean isRequired;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "courseRecord", fetch = FetchType.EAGER)
    @Valid
    private Collection<ModuleRecord> moduleRecords = new HashSet<>();

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime lastUpdated;

    public CourseRecord() {
    }

    @JsonCreator
    public CourseRecord(@NotBlank(message = "courseId is required") @JsonProperty("courseId") String courseId,
                        @NotBlank(message = "userId is required") @JsonProperty("userId") String userId) {
        this.identity = new CourseRecordIdentity(courseId, userId);
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public CourseRecordIdentity getIdentity() {
        return identity;
    }

    public void setIdentity(CourseRecordIdentity identity) {
        this.identity = identity;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @JsonProperty("courseId")
    public String getCourseId() {
        return identity.getCourseId();
    }

    @JsonProperty("userId")
    public String getUserId() {
        return identity.getUserId();
    }

    public Preference getPreference() {
        return preference;
    }

    public void setPreference(Preference preference) {
        this.preference = preference;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean isRequired) {
        this.isRequired = isRequired;
    }

    @JsonProperty("modules")
    public Collection<ModuleRecord> getModuleRecords() {
        return moduleRecords;
    }

    public void setModuleRecords(List<ModuleRecord> updatedModules) {
        this.moduleRecords = updatedModules;
    }

    public void addModuleRecord(ModuleRecord moduleRecord) {
        checkArgument(moduleRecord != null);
        moduleRecord.setCourseRecord(this);
        if (moduleRecords == null) {
            moduleRecords = new ArrayList<>();
        }
        moduleRecords.add(moduleRecord);
    }

    public ModuleRecord getModuleRecord(String moduleId) {
        return this.moduleRecords.stream()
                .filter(moduleRecord -> moduleId.equals(moduleRecord.getModuleId()))
                .findFirst()
                .orElse(null);
    }

    public LocalDateTime getCompletionDate() {
        LocalDateTime mostRecentCompletionDate = null;
        for (ModuleRecord moduleRecord : moduleRecords) {
            if (mostRecentCompletionDate == null ||
                    moduleRecord.getCompletionDate() != null
                            && mostRecentCompletionDate.isBefore(moduleRecord.getCompletionDate())) {
                mostRecentCompletionDate = moduleRecord.getCompletionDate();
            }
        }
        return mostRecentCompletionDate;
    }

    public boolean matchesActivityId(String activityId) {
        if (activityId.endsWith(getCourseId())) {
            return true;
        }
        for (ModuleRecord moduleRecord : moduleRecords) {
            if (activityId.endsWith(moduleRecord.getModuleId())) {
                return true;
            }
            String eventId = moduleRecord.getEventId();
            if (eventId != null && activityId.endsWith(eventId)) {
                return true;
            }
        }
        return false;
    }

    public void update(CourseRecord input) {
        this.state = input.getState();
        this.preference = input.getPreference();
        this.lastUpdated = input.getLastUpdated();
    }
}
