package uk.gov.cslearning.record.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.cslearning.record.domain.Booking;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.domain.Learner;

import javax.transaction.Transactional;

import java.time.Instant;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.notNullValue;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private LearnerRepository learnerRepository;

    @Autowired
    private EventRepository eventRepository;

    @Test
    public void shouldSaveBooking() {
        Learner learner = new Learner();
        learner.setUid("75c2c3b3-722f-4ffb-aec9-3d743a2d5330");

        Event event = new Event();
        event.setPath("test/path");
        event.setCatalogueId("SSB");

        learnerRepository.save(learner);
        eventRepository.save(event);

        Booking booking = new Booking(1);
        booking.setEventId(event.getId());
        booking.setLearnerId(learner.getId());
        booking.setStatus("CONFIRMED");
        booking.setPaymentDetails("payment/details");
        booking.setBookingTime(Instant.now());

        bookingRepository.save(booking);

        assertThat(booking.getId(), notNullValue());
    }
}
