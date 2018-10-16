package uk.gov.cslearning.record.service.catalogue;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.Assert.assertEquals;

public class EventTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void shouldDeserializeEventJson() throws IOException {
        Event event = objectMapper.readValue(getClass().getResource("/event.json"), Event.class);

        DateRange dateRange = event.getDateRanges().get(0);
        Venue venue = event.getVenue();

        assertEquals(LocalDate.of(2019, 3, 31), dateRange.getDate());
        assertEquals(LocalTime.of(9, 0), dateRange.getStartTime());
        assertEquals(LocalTime.of(17, 0), dateRange.getEndTime());


        assertEquals("London", venue.getLocation());
        assertEquals("Victoria Street", venue.getAddress());
        assertEquals(new Integer(10), venue.getCapacity());
        assertEquals(new Integer(5), venue.getMinCapacity());
    }
}
