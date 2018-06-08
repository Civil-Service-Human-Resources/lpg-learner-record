package uk.gov.cslearning.record.service;

public class CivilServant {

    private String departmentCode;

    private String fullName;

    private String gradeCode;

    private String lineManagerEmail;

    private String profession;

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public String getGradeCode() {
        return gradeCode;
    }

    public void setGradeCode(String gradeCode) {
        this.gradeCode = gradeCode;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLineManagerEmail() {
        return lineManagerEmail;
    }

    public void setLineManagerEmail(String lineManagerEmail) {
        this.lineManagerEmail = lineManagerEmail;
    }

}
