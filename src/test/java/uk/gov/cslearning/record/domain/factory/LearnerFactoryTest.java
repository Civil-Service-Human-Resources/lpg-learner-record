package uk.gov.cslearning.record.domain.factory;

import org.junit.Test;
import uk.gov.cslearning.record.domain.Booking;
import uk.gov.cslearning.record.domain.Learner;

import java.util.Collections;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

public class LearnerFactoryTest {
    private LearnerFactory learnerFactory = new LearnerFactory();

    @Test
    public void shouldReturnLearner() {
        Booking booking = new Booking();
        String learnerUuid = "learner-uuid";

        Learner learner = learnerFactory.create(learnerUuid, booking);

        assertThat(learner.getUuid(), equalTo(learnerUuid));
        assertThat(learner.getBookings(), equalTo(Collections.singletonList(booking)));
    }
}