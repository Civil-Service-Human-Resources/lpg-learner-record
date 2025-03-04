package uk.gov.cslearning.record.service.catalogue;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Slf4j
@Data
@AllArgsConstructor
public class Course {

    private String id;
    private String title;
    private Collection<Module> modules;
    private Collection<Audience> audiences;

    public Module getModule(String moduleId) {
        return modules.stream().filter(module -> moduleId.equals(module.getId())).findFirst().orElse(null);
    }

    public List<String> getRequiredModuleIds() {
        List<String> optionalModuleIds = new ArrayList<>();
        List<String> nonOptionalModuleIds = new ArrayList<>();
        modules.forEach(m -> {
            String id = m.getId();
            if (m.isOptional()) {
                optionalModuleIds.add(id);
            } else {
                nonOptionalModuleIds.add(id);
            }
        });
        return nonOptionalModuleIds.size() > 0 ? nonOptionalModuleIds : optionalModuleIds;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .toString();
    }

    public Event getEvent(String eventId) {
        for (Module module : this.modules) {
            Event event = module.getEvent(eventId);
            if (event != null) {
                return event;
            }
        }
        return null;
    }

}
