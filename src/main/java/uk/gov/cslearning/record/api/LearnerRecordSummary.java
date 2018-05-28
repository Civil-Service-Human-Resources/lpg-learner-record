package uk.gov.cslearning.record.api;

public class LearnerRecordSummary {

    private String courseIdentifier;

    private String courseName;

    private String moduleIdentifier;

    private String moduleName;

    private Long timeTaken;

    private String type;

    private Integer completed = 0;

    private Integer inProgress = 0;

    private Integer notStarted = 0;

    public LearnerRecordSummary() {
    }

    public String getCourseIdentifier() {
        return courseIdentifier;
    }

    public void setCourseIdentifier(String courseIdentifier) {
        this.courseIdentifier = courseIdentifier;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getModuleIdentifier() {
        return moduleIdentifier;
    }

    public void setModuleIdentifier(String moduleIdentifier) {
        this.moduleIdentifier = moduleIdentifier;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public Long getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(Long timeTaken) {
        this.timeTaken = timeTaken;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getCompleted() {
        return completed;
    }

    public Integer getInProgress() {
        return inProgress;
    }

    public Integer getNotStarted() {
        return notStarted;
    }

    public void incrementCompleted() {
        completed += 1;
    }

    public void incrementInProgress() {
        inProgress += 1;
    }

    public void incrementNotStarted() {
        notStarted += 1;
    }
}
