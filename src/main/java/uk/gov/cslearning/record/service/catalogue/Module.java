package uk.gov.cslearning.record.service.catalogue;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Collection;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
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

}
