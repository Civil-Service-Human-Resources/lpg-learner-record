package uk.gov.cslearning.record.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;
import java.util.HashSet;

import static com.google.gson.internal.$Gson$Preconditions.checkArgument;
import static java.util.Collections.unmodifiableCollection;

public class CourseRecord {

    private String courseId;

    private String userId;

    private Collection<ModuleRecord> moduleRecords;

    public CourseRecord(String courseId, String userId) {
        checkArgument(courseId != null);
        checkArgument(userId != null);
        this.courseId = courseId;
        this.userId = userId;
        this.moduleRecords = new HashSet<>();
    }

    public String getCourseId() {
        return courseId;
    }

    public String getUserId() {
        return userId;
    }

    @JsonProperty("modules")
    public Collection<ModuleRecord> getModuleRecords() {
        return unmodifiableCollection(moduleRecords);
    }

    public void addModuleRecord(ModuleRecord moduleRecord) {
        checkArgument(moduleRecord != null);
        moduleRecords.add(moduleRecord);
    }
}
