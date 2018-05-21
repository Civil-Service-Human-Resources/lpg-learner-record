package uk.gov.cslearning.record.service.catalogue;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import uk.gov.cslearning.record.service.CivilServant;

import java.time.LocalDateTime;
import java.util.List;

public class Audience {

    private List<String> areasOfWork;

    private List<String> departments;

    private List<String> grades;

    private String frequency;

    private boolean mandatory;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime requiredBy;

    public List<String> getAreasOfWork() {
        return areasOfWork;
    }

    public void setAreasOfWork(List<String> areasOfWork) {
        this.areasOfWork = areasOfWork;
    }

    public List<String> getDepartments() {
        return departments;
    }

    public void setDepartments(List<String> departments) {
        this.departments = departments;
    }

    public List<String> getGrades() {
        return grades;
    }

    public void setGrades(List<String> grades) {
        this.grades = grades;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public LocalDateTime getRequiredBy() {
        return requiredBy;
    }

    public void setRequiredBy(LocalDateTime requiredBy) {
        this.requiredBy = requiredBy;
    }

    public int getRelevance(CivilServant civilServant) {
        int relevance = 0;
        if (areasOfWork != null && areasOfWork.contains(civilServant.getAreaOfWork())) {
            relevance += 1;
        }
        if (departments != null && departments.contains(civilServant.getDepartmentCode())) {
            relevance += 1;
        }
        if (grades != null && grades.contains(civilServant.getGradeCode())) {
            relevance += 1;
        }
        return relevance;
    }

    public LocalDateTime getNextRequiredBy(LocalDateTime completionDate) {
        if (frequency == null || requiredBy == null) {
            return null;
        }
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime nextRequiredBy = requiredBy;
        while (nextRequiredBy.isBefore(today)) {
            nextRequiredBy = increment(nextRequiredBy, frequency);
        }
        LocalDateTime lastRequiredBy = decrement(nextRequiredBy, frequency);

        if (completionDate != null && completionDate.isAfter(lastRequiredBy)) {
            return increment(nextRequiredBy, frequency);
        }
        return nextRequiredBy;
    }

    private LocalDateTime decrement(LocalDateTime dateTime, String frequency) {
        return dateTime.minusYears(getYears(frequency));
    }

    private LocalDateTime increment(LocalDateTime dateTime, String frequency) {
        return dateTime.plusYears(getYears(frequency));
    }

    private long getYears(String frequency) {
        switch (frequency) {
            case "YEARLY":
                return 1;
            case "THREE_YEARLY":
                return 3;
            case "FIVE_YEARLY":
                return 5;
            default:
                throw new RuntimeException("Unrecognised frequency " + frequency);
        }
    }
}
