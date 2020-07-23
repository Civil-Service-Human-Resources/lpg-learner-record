package uk.gov.cslearning.record.csrs.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

import uk.gov.cslearning.record.service.identity.Identity;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CivilServant {

    private String fullName;
    private Grade grade = new Grade();
    private OrganisationalUnit organisationalUnit = new OrganisationalUnit();
    private Profession profession = new Profession();
    private JobRole jobRole = new JobRole();
    private List<Profession> otherAreasOfWork = new ArrayList<>();
    private List<Interest> interests = new ArrayList<>();
    private String lineManagerEmailAddress;
    private String lineManagerUid;
    private String lineManagerName;
    private Identity identity;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Grade getGrade() {
        return grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }

    public OrganisationalUnit getOrganisationalUnit() {
        return organisationalUnit;
    }

    public void setOrganisationalUnit(OrganisationalUnit organisationalUnit) {
        this.organisationalUnit = organisationalUnit;
    }

    public Profession getProfession() {
        return profession;
    }

    public void setProfession(Profession profession) {
        this.profession = profession;
    }

    public JobRole getJobRole() {
        return jobRole;
    }

    public void setJobRole(JobRole jobRole) {
        this.jobRole = jobRole;
    }

    public List<Profession> getOtherAreasOfWork() {
        return otherAreasOfWork;
    }

    public void setOtherAreasOfWork(List<Profession> otherAreasOfWork) {
        this.otherAreasOfWork = otherAreasOfWork;
    }

    public List<Interest> getInterests() {
        return interests;
    }

    public void setInterests(List<Interest> interests) {
        this.interests = interests;
    }

    public String getLineManagerEmailAddress() {
        return lineManagerEmailAddress;
    }

    public void setLineManagerEmailAddress(String lineManagerEmailAddress) {
        this.lineManagerEmailAddress = lineManagerEmailAddress;
    }

    public String getLineManagerUid() {
        return lineManagerUid;
    }

    public void setLineManagerUid(String lineManagerUid) {
        this.lineManagerUid = lineManagerUid;
    }

    public String getLineManagerName() {
        return lineManagerName;
    }

    public void setLineManagerName(String lineManagerName) {
        this.lineManagerName = lineManagerName;
    }

    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
    }
}
