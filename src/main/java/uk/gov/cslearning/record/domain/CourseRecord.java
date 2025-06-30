package uk.gov.cslearning.record.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.gson.internal.$Gson$Preconditions.checkArgument;

@Entity
@Getter
@Setter
public class CourseRecord {

    @JsonIgnore
    @EmbeddedId
    @Valid
    private CourseRecordIdentity identity;

    @NotBlank(message = "courseTitle is required")
    private String courseTitle;

    @Enumerated(EnumType.STRING)
    private State state;

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

    @JsonProperty("courseId")
    public String getCourseId() {
        return identity.getCourseId();
    }

    @JsonProperty("userId")
    public String getUserId() {
        return identity.getUserId();
    }

    @JsonProperty("modules")
    public Collection<ModuleRecord> getModuleRecords() {
        return moduleRecords;
    }
    
    public void addModuleRecord(ModuleRecord moduleRecord) {
        checkArgument(moduleRecord != null);
        moduleRecord.setCourseRecord(this);
        if (moduleRecords == null) {
            moduleRecords = new ArrayList<>();
        }
        moduleRecords.add(moduleRecord);
    }

    @JsonIgnore
    public LocalDateTime getEarliestCompletionDateForModules(List<String> moduleIds) {
        Map<String, ModuleRecord> map = this.moduleRecords.stream().collect(Collectors.toMap(ModuleRecord::getModuleId, mr -> mr));
        return moduleIds.stream().map(moduleId -> {
            ModuleRecord mr = map.get(moduleId);
            if (mr == null || mr.getCompletionDate() == null) {
                return LocalDateTime.MIN;
            } else {
                return mr.getCompletionDate();
            }
        }).min(LocalDateTime::compareTo).orElse(LocalDateTime.MIN);
    }

}
