package uk.gov.cslearning.record.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.cslearning.record.domain.Booking;

import javax.transaction.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.notNullValue;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    public void shouldSaveBooking() {
        Booking booking = new Booking(new Long(1), new Long(2));
        bookingRepository.save(booking);

        assertThat(booking.getLearnerId(), notNullValue());
        assertThat(booking.getEventId(), notNullValue());
    }
}
