package uk.gov.cslearning.record.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;

import static com.google.gson.internal.$Gson$Preconditions.checkArgument;
import static java.util.Collections.unmodifiableCollection;

@Entity
public class CourseRecord {

    @JsonIgnore
    @EmbeddedId
    private CourseRecordIdentity identity;

    private String courseTitle;

    @Enumerated(EnumType.STRING)
    private State state;

    private String preference;

    @JsonIgnore
    private String profession;

    @JsonIgnore
    private String department;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "courseRecord")
    private Collection<ModuleRecord> moduleRecords;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime lastUpdated;

    public CourseRecord() {
    }

    public CourseRecord(String courseId, String userId) {
        checkArgument(courseId != null);
        checkArgument(userId != null);
        this.identity = new CourseRecordIdentity(courseId, userId);
        this.moduleRecords = new HashSet<>();
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

    public String getPreference() {
        return preference;
    }

    public void setPreference(String preference) {
        this.preference = preference;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @JsonProperty("modules")
    public Collection<ModuleRecord> getModuleRecords() {
        return unmodifiableCollection(moduleRecords);
    }

    public void setModuleRecords(Collection<ModuleRecord> moduleRecords) {
        this.moduleRecords = moduleRecords;
    }

    public void addModuleRecord(ModuleRecord moduleRecord) {
        checkArgument(moduleRecord != null);
        moduleRecord.setCourseRecord(this);
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

    public boolean isComplete() {
        return this.state == State.COMPLETED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CourseRecord that = (CourseRecord) o;

        return new EqualsBuilder()
                .append(identity, that.identity)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(identity)
                .toHashCode();
    }
}
