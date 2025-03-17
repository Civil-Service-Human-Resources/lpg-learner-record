package uk.gov.cslearning.record.service.catalogue;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Event {

    private String id;
    private List<DateRange> dateRanges = new ArrayList<>();
    private Venue venue;

    @JsonIgnore
    public String getFirstDateAsString() {
        return this.getFirstDate().format(DateTimeFormatter.ofPattern("dd MMM uuuu"));
    }

    @JsonIgnore
    public LocalDate getFirstDate() {
        return getDateRanges().get(0).getDate();
    }
}
