package uk.gov.cslearning.record.repository;

import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.cslearning.record.domain.Booking;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.domain.Learner;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.notNullValue;
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
        learner.setUuid("75c2c3b3-722f-4ffb-aec9-3d743a2d5330");

        Event event = new Event();
        event.setPath("test/path");

        Booking booking = new Booking();
        booking.setEvent(event);
        booking.setLearner(learner);
        booking.setStatus("CONFIRMED");
        booking.setPaymentDetails("payment/details");
        booking.setBookingTime(LocalDateTime.now());

        booking = bookingRepository.save(booking);

        event.setBookings(Lists.newArrayList(booking));
        learner.setBookings(Lists.newArrayList(booking));

        eventRepository.save(event);
        learnerRepository.save(learner);

        Booking savedBooking = bookingRepository.findById(booking.getId()).get();
        assertEquals(event, savedBooking.getEvent());
        assertEquals(learner, savedBooking.getLearner());

        assertEquals(Collections.singletonList(booking), learner.getBookings());
        assertEquals(Collections.singletonList(booking), event.getBookings());
    }
}
