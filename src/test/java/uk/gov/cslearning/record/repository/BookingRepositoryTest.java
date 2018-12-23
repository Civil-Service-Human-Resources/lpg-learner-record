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
}
