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
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

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
    public void shouldFindBookingsCreatedBetweenDates() {
        String learnerUid = "75c2c3b3-722f-4ffb-aec9-3d743a2d5330";
        String eventUid = "SSB";

        Instant baseInstant = ZonedDateTime.of(LocalDate.now().atStartOfDay(), ZoneId.systemDefault()).toInstant();

        Learner learner = new Learner();
        learner.setUid(learnerUid);
        learner.setLearnerEmail("test@domain.com");

        Event event = new Event();
        event.setPath("test/path");
        event.setUid(eventUid);

        Booking booking1 = new Booking();
        booking1.setEvent(event);
        booking1.setLearner(learner);
        booking1.setBookingTime(baseInstant.minus(1, ChronoUnit.DAYS));

        Booking booking2 = new Booking();
        booking2.setEvent(event);
        booking2.setLearner(learner);
        booking2.setBookingTime(baseInstant.minus(2, ChronoUnit.DAYS));

        Booking booking3 = new Booking();
        booking3.setEvent(event);
        booking3.setLearner(learner);
        booking3.setBookingTime(baseInstant.minus(3, ChronoUnit.DAYS));

        Booking booking4 = new Booking();
        booking4.setEvent(event);
        booking4.setLearner(learner);
        booking4.setBookingTime(baseInstant.minus(4, ChronoUnit.DAYS));

        Booking booking5 = new Booking();
        booking5.setEvent(event);
        booking5.setLearner(learner);
        booking5.setBookingTime(baseInstant.minus(5, ChronoUnit.DAYS));

        bookingRepository.saveAll(Arrays.asList(booking1, booking2, booking3, booking4, booking5));

        Instant from = baseInstant.minus(4, ChronoUnit.DAYS);
        Instant to = baseInstant.minus(2, ChronoUnit.DAYS);

        List<Booking> bookings = bookingRepository.findAllByBookingTimeBetween(from, to);

        assertEquals(3, bookings.size());
        assertEquals(Arrays.asList(booking2, booking3, booking4), bookings);
    }

    @Test
    public void shouldDeleteBookingsByLearner() {
        Event event = new Event();
        event.setUid("SDBBL");
        event.setPath("test/path");

        Instant baseInstant = ZonedDateTime.of(LocalDate.now().atStartOfDay(), ZoneId.systemDefault()).toInstant();

        Learner learner1 = new Learner();
        learner1.setUid("learner1-test-uid");
        learner1.setLearnerEmail("test@domain.com");

        Learner learner2 = new Learner();
        learner2.setUid("learner2-test-uid");
        learner2.setLearnerEmail("test2@domain.com");

        Booking booking1 = new Booking();
        booking1.setLearner(learner1);
        booking1.setBookingTime(baseInstant.minus(1, ChronoUnit.DAYS));
        booking1.setEvent(event);

        Booking booking2 = new Booking();
        booking2.setLearner(learner1);
        booking2.setBookingTime(baseInstant.minus(2, ChronoUnit.DAYS));
        booking2.setEvent(event);

        Booking booking3 = new Booking();
        booking3.setLearner(learner2);
        booking3.setBookingTime(baseInstant.minus(3, ChronoUnit.DAYS));
        booking3.setEvent(event);

        bookingRepository.saveAll(Arrays.asList(booking1, booking2, booking3));

        bookingRepository.deleteAllByLearner(learner1);

        List<Booking> bookings = bookingRepository.findAll();

        assertEquals(1, bookings.size());
        assertEquals(booking3, bookings.get(0));
    }
}
