package uk.gov.cslearning.record.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import uk.gov.cslearning.record.service.xapi.activity.Activity;

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

    @Enumerated(EnumType.STRING)
    private State state;

    private String preference;

    @JsonIgnore
    private String profession;

    @JsonIgnore
    private String department;

    @OneToMany(cascade = CascadeType.ALL)
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

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @JsonProperty("modules")
    public Collection<ModuleRecord> getModuleRecords() {
        return unmodifiableCollection(moduleRecords);
    }

    public void addModuleRecord(ModuleRecord moduleRecord) {
        checkArgument(moduleRecord != null);
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
        if (activityId.startsWith(Activity.COURSE_ID_PREFIX)) {
            return activityId.endsWith(getCourseId());
        }
        for (ModuleRecord moduleRecord : moduleRecords) {
            if (activityId.endsWith(moduleRecord.getModuleId())) {
                return true;
            }
        }
        return false;
    }
}
