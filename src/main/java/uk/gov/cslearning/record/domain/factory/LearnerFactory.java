package uk.gov.cslearning.record.domain.factory;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.domain.Booking;
import uk.gov.cslearning.record.domain.Learner;

@Component
public class LearnerFactory {
    public Learner create(String uuid, Booking booking) {
        Learner learner = new Learner();
        learner.setUuid(uuid);
        learner.addToBookings(booking);

        return learner;
    }
}
