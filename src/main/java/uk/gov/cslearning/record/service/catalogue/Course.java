package uk.gov.cslearning.record.service.catalogue;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.cslearning.record.api.LearnerRecordController;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.domain.State;
import uk.gov.cslearning.record.service.CivilServant;

import java.time.LocalDate;
import java.util.*;

public class Course {

    private String id;

    private String title;

    private Collection<Module> modules;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(Course.class);

    public LocalDate getNextRequiredBy(CivilServant civilServant, LocalDate completionDate) {
        LocalDate nextRequiredBy = null;

        for (Module module : modules) {
            LocalDate moduleNextRequiredBy = module.getNextRequiredBy(civilServant, completionDate);
            if (nextRequiredBy == null) {
                nextRequiredBy = moduleNextRequiredBy;
            } else if (moduleNextRequiredBy != null && moduleNextRequiredBy.isBefore(nextRequiredBy)) {
                nextRequiredBy = moduleNextRequiredBy;
            }
        }
        return nextRequiredBy;
    }

    public Module getModule(String moduleId) {
        return modules.stream().filter(module -> moduleId.equals(module.getId())).findFirst().orElse(null);
    }

    public Boolean isComplete(Collection<CourseRecord> courseRecords, CivilServant civilServant) {
        Collection<State> states = new HashSet<>();
        states.add(State.COMPLETED);
        LOGGER.info("Checking that course {} is complete against {} records", this.getId(), courseRecords.size());
        return this.checkModuleStates(courseRecords , civilServant, states,true ,   true);
    }

    public Collection<Module> getModulesForUser(CivilServant civilServant) {
        Collection<Module> modules = new HashSet<>();

        for (Module module :this.getModules()) {
            if(module.getMostRelevantAudienceFor(civilServant) != null) {
                modules.add(module);
            }
        }

        return modules;
    }


    public Boolean checkModuleStates(Collection<CourseRecord> courseRecords, CivilServant civilServant, Collection<State> states, Boolean mustHave, Boolean onlyMandatory)     {
        Boolean hasModuleRecord = false;

        for (CourseRecord courseRecord : courseRecords) {
            LOGGER.info("checking {} against {}", courseRecord.get(), this.get());
        }

        Optional<CourseRecord> optionalCourseRecord = courseRecords.stream().filter(a -> a.getCourseId().equals(this.getId())).findFirst(); // get courseRecord

        if (optionalCourseRecord.isPresent()) {
            LOGGER.info("Found record for course {}", this.getId());
            CourseRecord courseRecord = optionalCourseRecord.get();
            Collection<Module> modules = getModulesForUser(civilServant);

            for ( Module module : modules) {
                LOGGER.info("Checking  module {} state", module.getId());
                Audience audience = module.getMostRelevantAudienceFor(civilServant);
                Boolean mandatory = audience.isMandatory();

                ModuleRecord moduleRecord = courseRecord.getModuleRecord(module.getId());

                hasModuleRecord = moduleRecord != null || hasModuleRecord ? true : false ;

                if (moduleRecord != null && moduleRecord.getState() != null && (!onlyMandatory || mandatory)) {
                    LOGGER.info("Record for module  {} found. State is {} ", module.getId(), moduleRecord.getState());
                    if (!states.stream().filter(state -> state == moduleRecord.getState()).findFirst().isPresent() && mustHave) {
                        LOGGER.info("FAIL: Module {} state does not match required state(s)!", module.getId());
                        return false;
                    } else if (states.stream().filter(state -> state == moduleRecord.getState()).findFirst().isPresent() && !mustHave) {
                        LOGGER.info("PASS: Module {} state matches an optional state", module.getId());
                        return true;
                    }

                } else if (mandatory) {
                    LOGGER.info("FAIL: Module {} lacks a needed record!", module.getId());
                    return false;
                }
            }
            if (hasModuleRecord) {
                LOGGER.info("PASS: At least one module had a record in state check");
                return true;
            }

        }
        LOGGER.info("FAIL");
        return false;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .toString();
    }
}
