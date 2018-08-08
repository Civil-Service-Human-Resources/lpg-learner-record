package uk.gov.cslearning.record.service.catalogue;

import com.google.common.collect.Iterables;
import uk.gov.cslearning.record.service.CivilServant;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;

public class Module {

    private String id;

    private String title;

    private String moduleType;

    private Long duration;

    private BigDecimal cost;

    private Collection<Event> events;

    private boolean optional;

    public Event getEvent(String eventId) {
        return events.stream().filter(event -> eventId.equals(event.getId())).findFirst().orElse(null);
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public Collection<Event> getEvents() {
        return events;
    }

    public void setEvents(Collection<Event> events) {
        this.events = events;
    }

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

    public String getModuleType() {
        return moduleType;
    }

    public void setModuleType(String moduleType) {
        this.moduleType = moduleType;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }



    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }
}
