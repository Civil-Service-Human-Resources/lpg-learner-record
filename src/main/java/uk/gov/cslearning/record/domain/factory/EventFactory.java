package uk.gov.cslearning.record.domain.factory;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.domain.Booking;
import uk.gov.cslearning.record.domain.Event;

@Component
public class EventFactory {

    public Event create(String path, Booking booking) {
        Event event = new Event();
        event.setPath(path);
        event.addToBookings(booking);

        return event;
    }

}
