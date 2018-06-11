package uk.gov.cslearning.record.service.catalogue;

import org.apache.commons.lang3.builder.ToStringBuilder;
import uk.gov.cslearning.record.service.CivilServant;

import java.time.LocalDate;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .toString();
    }
}
