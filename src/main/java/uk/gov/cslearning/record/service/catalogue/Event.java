package uk.gov.cslearning.record.service.catalogue;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Event {

    private String id;

    private List<DateRange> dateRanges = new ArrayList<>();

    private Venue venue;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public List<DateRange> getDateRanges() {
        return Collections.unmodifiableList(dateRanges);
    }

    public void setDateRanges(List<DateRange> dateRanges) {
        this.dateRanges = Collections.unmodifiableList(dateRanges);
    }
}
