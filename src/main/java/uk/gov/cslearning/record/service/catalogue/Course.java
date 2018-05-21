package uk.gov.cslearning.record.service.catalogue;

import uk.gov.cslearning.record.service.CivilServant;

import java.time.LocalDateTime;
import java.util.Collection;

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

    public LocalDateTime getNextRequiredBy(CivilServant civilServant, LocalDateTime completionDate) {
        LocalDateTime nextRequiredBy = null;

        for (Module module : modules) {
            LocalDateTime moduleNextRequiredBy = module.getNextRequiredBy(civilServant, completionDate);
            if (nextRequiredBy == null) {
                nextRequiredBy = moduleNextRequiredBy;
            } else if (moduleNextRequiredBy != null && moduleNextRequiredBy.isBefore(nextRequiredBy)) {
                nextRequiredBy = moduleNextRequiredBy;
            }
        }
        return nextRequiredBy;
    }
}
