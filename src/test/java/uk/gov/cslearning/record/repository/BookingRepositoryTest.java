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
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

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
        learner.setLearnerEmail("test@domain.com");

        Event event = new Event();
        event.setPath("test/path");
        event.setEventUid("SSB");

        Booking booking = new Booking();
        booking.setEvent(event);
        booking.setLearner(learner);
        booking.setStatus("CONFIRMED");
        booking.setPaymentDetails("payment/details");
        booking.setBookingTime(Instant.now());

        booking = bookingRepository.save(booking);

        Booking savedBooking = bookingRepository.findById(booking.getId()).get();
        assertEquals(event, savedBooking.getEvent());
        assertEquals(learner, savedBooking.getLearner());
    }

    @Test
    public void shouldListByEventId() {
        Learner learner = new Learner();
        learner.setUid("75c2c3b3-722f-4ffb-aec9-3d743a2d5330");
        learner.setLearnerEmail("test@domain.com");

        Learner learner2 = new Learner();
        learner2.setUid("82a2j3b6-822f-4ffb-tnb9-4d734e01v721");
        learner2.setLearnerEmail("test@domain.com");

        Event event = new Event();
        event.setPath("test/path");
        event.setEventUid("SLBEI");

        Booking booking = new Booking();
        booking.setEvent(event);
        booking.setLearner(learner);
        booking.setStatus("CONFIRMED");
        booking.setPaymentDetails("payment/details/1");
        booking.setBookingTime(Instant.now());

        Booking booking2 = new Booking();
        booking2.setEvent(event);
        booking2.setLearner(learner2);
        booking2.setStatus("REQUESTED");
        booking2.setPaymentDetails("payment/details/2");
        booking2.setBookingTime(Instant.now());

        bookingRepository.save(booking);
        bookingRepository.save(booking2);

        ArrayList<Booking> bookings = (ArrayList) bookingRepository.listByEventId("SLBEI");

        assertEquals(bookings.get(0), booking);
        assertEquals(bookings.get(1), booking2);
    }
}
