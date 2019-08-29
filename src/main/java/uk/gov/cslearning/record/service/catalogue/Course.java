package uk.gov.cslearning.record.service.catalogue;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.cslearning.record.csrs.domain.CivilServant;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.domain.State;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Course {
    private static final Logger LOGGER = LoggerFactory.getLogger(Course.class);

    private String id;
    private String title;
    private Collection<Module> modules;
    private Collection<Audience> audiences;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Collection<Module> getModules() {
        return modules;
    }

    public void setModules(Collection<Module> modules) {
        this.modules = modules;
    }

    public Module getModule(String moduleId) {
        return modules.stream().filter(module -> moduleId.equals(module.getId())).findFirst().orElse(null);
    }

    public Collection<Audience> getAudiences() {
        return audiences;
    }

    public void setAudiences(Collection<Audience> audiences) {
        this.audiences = audiences;
    }

    public Boolean isComplete(Collection<CourseRecord> courseRecords) {
        Collection<State> states = new HashSet<>();
        states.add(State.COMPLETED);
        LOGGER.info("Checking that course {} is complete against {} records", this.getId(), courseRecords.size());
        return this.checkModuleStates(courseRecords, states, true, true);
    }

    private Boolean checkModuleStates(Collection<CourseRecord> courseRecords, Collection<State> states, Boolean mustHave, Boolean onlyMandatory) {
        Boolean hasModuleRecord = false;

        Optional<CourseRecord> optionalCourseRecord = courseRecords.stream().filter(a -> a.getCourseId().equals(this.getId())).findFirst(); // get courseRecord

        if (optionalCourseRecord.isPresent()) {
            LOGGER.info("Found record for course {}", this.getId());
            CourseRecord courseRecord = optionalCourseRecord.get();

            for (Module module : modules) {
                Boolean mandatory = !module.isOptional();
                LOGGER.info("Checking  module {} which is  {}.", module.getId(), mandatory ? "MANDATORY" : "NOT MANDATORY");

                ModuleRecord moduleRecord = courseRecord.getModuleRecord(module.getId());

                hasModuleRecord = moduleRecord != null || hasModuleRecord;

                if (moduleRecord != null && moduleRecord.getState() != null && (!onlyMandatory || mandatory)) {
                    LOGGER.info("Record for module  {} found. State is {} ", module.getId(), moduleRecord.getState());
                    if (states.stream().noneMatch(state -> state == moduleRecord.getState()) && mustHave) {
                        LOGGER.info("FAIL: Module {} state does not match required state(s)!", module.getId());
                        return false;
                    } else if (states.stream().anyMatch(state -> state == moduleRecord.getState()) && !mustHave) {
                        LOGGER.info("PASS: Module {} state matches an optional state", module.getId());
                        return true;
                    }

                } else if (mandatory) {
                    LOGGER.info("FAIL: Module {} lacks a needed record!", module.getId());
                    return false;
                }
            }
            if (hasModuleRecord) {
                LOGGER.info("PASS: No fails in mandatory modules or none pristine optional course.");
                return true;
            }
        }

        LOGGER.info("FAIL");
        return false;
    }

    public LocalDate getNextRequiredBy(CivilServant civilServant, LocalDate completionDate) {
        Audience audience = getMostRelevantAudienceFor(civilServant);
        if (audience != null) {
            return audience.getNextRequiredBy(completionDate);
        }
        return null;
    }

    private Audience getMostRelevantAudienceFor(CivilServant civilServant) {
        int highestRelevance = -1;
        Audience mostRelevantAudience = null;

        for (Audience audience : audiences) {
            int audienceRelevance = audience.getRelevance(civilServant);
            if (audienceRelevance > highestRelevance) {
                mostRelevantAudience = audience;
                highestRelevance = audienceRelevance;
            }
        }
        if (highestRelevance > -1) {
            return mostRelevantAudience;
        }
        return null;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .toString();
    }
}
