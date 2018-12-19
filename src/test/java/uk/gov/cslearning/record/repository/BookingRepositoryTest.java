package uk.gov.cslearning.record.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.cslearning.record.domain.Booking;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.domain.Learner;
import uk.gov.cslearning.record.dto.BookingStatus;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    public void shouldSaveBooking() {
        Learner learner = new Learner();
        learner.setUid("75c2c3b3-722f-4ffb-aec9-3d743a2d5330");
        learner.setLearnerEmail("test@domain.com");

        Event event = new Event();
        event.setPath("test/path");
        event.setUid("SSB");

        Booking booking = new Booking();
        booking.setEvent(event);
        booking.setLearner(learner);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setPaymentDetails("payment/details");
        booking.setBookingTime(Instant.now());

        booking = bookingRepository.save(booking);

        Booking savedBooking = bookingRepository.findById(booking.getId()).get();
        assertEquals(event, savedBooking.getEvent());
        assertEquals(learner, savedBooking.getLearner());
    }

    @Test
    public void shouldFindBookingFromEventUidAndLearnerUid() {
        String learnerUid = "75c2c3b3-722f-4ffb-aec9-3d743a2d5330";
        String eventUid = "SSB";

        Learner learner = new Learner();
        learner.setUid(learnerUid);
        learner.setLearnerEmail("test@domain.com");

        Event event = new Event();
        event.setPath("test/path");
        event.setUid(eventUid);

        Booking booking = new Booking();
        booking.setEvent(event);
        booking.setLearner(learner);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setPaymentDetails("payment/details");
        booking.setBookingTime(Instant.now());

        Booking savedBooking = bookingRepository.save(booking);

        Optional<Booking> optional = bookingRepository.findByEventUidLearnerUid(eventUid, learnerUid);

        assertTrue(optional.isPresent());

        assertEquals(savedBooking.getId(), optional.get().getId());
    }
}
