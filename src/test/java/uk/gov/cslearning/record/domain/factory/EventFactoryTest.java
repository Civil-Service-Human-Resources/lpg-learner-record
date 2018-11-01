package uk.gov.cslearning.record.domain.factory;

import org.junit.Test;
import uk.gov.cslearning.record.domain.Booking;
import uk.gov.cslearning.record.domain.Event;

import java.util.Collections;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

public class EventFactoryTest {
    private EventFactory eventFactory = new EventFactory();

    @Test
    public void shouldReturnEvent() {
        Booking booking = new Booking();
        String eventPath = "event-path";

        Event event = eventFactory.create(eventPath, booking);

        assertThat(event.getBookings(), equalTo(Collections.singletonList(booking)));
        assertThat(event.getPath(), equalTo(eventPath));
    }
}