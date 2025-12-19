package uk.gov.cslearning.record.repository;


import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.cslearning.record.IntegrationTestBase;
import uk.gov.cslearning.record.domain.Booking;
import uk.gov.cslearning.record.domain.BookingStatus;
import uk.gov.cslearning.record.domain.Event;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@Transactional
public class BookingRepositoryTest extends IntegrationTestBase {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private EventRepository eventRepository;

    @Test
    public void shouldSaveBooking() {
        Event event = new Event();
        event.setUid("SSB");

        eventRepository.save(event);

        Booking booking = new Booking();
        booking.setEvent(event);
        booking.setLearnerUid("75c2c3b3-722f-4ffb-aec9-3d743a2d5330");
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setPaymentDetails("payment/details");
        booking.setBookingTime(Instant.now());

        booking = bookingRepository.save(booking);

        Booking savedBooking = bookingRepository.findById(booking.getId()).get();
        assertEquals(event, savedBooking.getEvent());
        assertEquals("75c2c3b3-722f-4ffb-aec9-3d743a2d5330", savedBooking.getLearnerUid());
    }

    @Test
    public void shouldFindBookingsCreatedBetweenDates() {
        String learnerUid = "75c2c3b3-722f-4ffb-aec9-3d743a2d5330";
        String eventUid = "SSB";

        Instant baseInstant = ZonedDateTime.of(LocalDate.now().atStartOfDay(), ZoneId.systemDefault()).toInstant();

        Event event = new Event();
        event.setUid(eventUid);

        eventRepository.save(event);

        Booking booking1 = new Booking();
        booking1.setEvent(event);
        booking1.setLearnerUid(learnerUid);
        booking1.setBookingTime(baseInstant.minus(1, ChronoUnit.DAYS));

        Booking booking2 = new Booking();
        booking2.setEvent(event);
        booking2.setLearnerUid(learnerUid);
        booking2.setBookingTime(baseInstant.minus(2, ChronoUnit.DAYS));

        Booking booking3 = new Booking();
        booking3.setEvent(event);
        booking3.setLearnerUid(learnerUid);
        booking3.setBookingTime(baseInstant.minus(3, ChronoUnit.DAYS));

        Booking booking4 = new Booking();
        booking4.setEvent(event);
        booking4.setLearnerUid(learnerUid);
        booking4.setBookingTime(baseInstant.minus(4, ChronoUnit.DAYS));

        Booking booking5 = new Booking();
        booking5.setEvent(event);
        booking5.setLearnerUid(learnerUid);
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

        eventRepository.save(event);

        Instant baseInstant = ZonedDateTime.of(LocalDate.now().atStartOfDay(), ZoneId.systemDefault()).toInstant();

        Booking booking1 = new Booking();
        booking1.setLearnerUid("learner1-test-uid");
        booking1.setBookingTime(baseInstant.minus(1, ChronoUnit.DAYS));
        booking1.setEvent(event);

        Booking booking2 = new Booking();
        booking2.setLearnerUid("learner1-test-uid");
        booking2.setBookingTime(baseInstant.minus(2, ChronoUnit.DAYS));
        booking2.setEvent(event);

        Booking booking3 = new Booking();
        booking3.setLearnerUid("learner2-test-uid");
        booking3.setBookingTime(baseInstant.minus(3, ChronoUnit.DAYS));
        booking3.setEvent(event);

        bookingRepository.saveAll(Arrays.asList(booking1, booking2, booking3));

        bookingRepository.deleteAllByLearnerUid("learner1-test-uid");

        List<Booking> bookings = bookingRepository.findAll();

        assertEquals(1, bookings.size());
        assertEquals(booking3, bookings.get(0));
    }
}
