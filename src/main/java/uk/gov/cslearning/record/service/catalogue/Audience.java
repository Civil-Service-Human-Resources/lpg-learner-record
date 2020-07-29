package uk.gov.cslearning.record.service.catalogue;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import uk.gov.cslearning.record.csrs.domain.CivilServant;

import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Audience {
    public enum Type {
        OPEN,
        CLOSED_COURSE,
        PRIVATE_COURSE,
        REQUIRED_LEARNING
    }

    private List<String> areasOfWork;

    private List<String> departments;

    private List<String> grades;

    private String frequency;

    private Type type;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate requiredBy;

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

    public LocalDate getRequiredBy() {
        return requiredBy;
    }

    public void setRequiredBy(LocalDate requiredBy) {
        this.requiredBy = requiredBy;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getRelevance(CivilServant civilServant) {
        int relevance = 0;
        if (areasOfWork != null && areasOfWork.contains(civilServant.getProfession().getName())) {
            relevance += 1;
        }
        if (departments != null && departments.contains(civilServant.getOrganisationalUnit().getCode())) {
            relevance += 1;
        }
        if (grades != null && grades.contains(civilServant.getGrade().getCode())) {
            relevance += 1;
        }
        return relevance;
    }

    public LocalDate getNextRequiredBy(LocalDate completionDate) {
        LocalDate today = LocalDate.now();
        if (requiredBy == null) {
            return null;
        }
        if (frequency == null) {
            if (requiredBy.isAfter(today) || requiredBy.isEqual(today)){
               return requiredBy;
            }
            return null;
        }
        LocalDate nextRequiredBy = requiredBy;
        while (nextRequiredBy.isBefore(today)) {
            nextRequiredBy = increment(nextRequiredBy, frequency);
        }
        LocalDate lastRequiredBy = decrement(nextRequiredBy, frequency);

        if (completionDate != null && completionDate.isAfter(lastRequiredBy)) {
            return increment(nextRequiredBy, frequency);
        }
        return nextRequiredBy;
    }

    private LocalDate decrement(LocalDate dateTime, String frequency) {
        return dateTime.minusYears(getYears(frequency));
    }

    private LocalDate increment(LocalDate dateTime, String frequency) {
        return dateTime.plusYears(getYears(frequency));
    }

    private long getYears(String frequency) {
        switch (frequency) {
            case "P1Y":
                return 1;
            case "P3Y":
                return 3;
            case "P5Y":
                return 5;
            default:
                throw new RuntimeException("Unrecognised frequency " + frequency);
        }
    }
}
