package uk.gov.cslearning.record.service.catalogue;

import uk.gov.cslearning.record.service.CivilServant;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;

public class Module {

    private String id;

    private String title;

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

    public Collection<Audience> getAudiences() {
        return audiences;
    }

    public void setAudiences(Collection<Audience> audiences) {
        this.audiences = audiences;
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
            }
        }
        return mostRelevantAudience;
    }
}
