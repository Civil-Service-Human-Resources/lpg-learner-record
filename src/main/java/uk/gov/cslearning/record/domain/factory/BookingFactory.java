package uk.gov.cslearning.record.domain.factory;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.domain.Booking;
import uk.gov.cslearning.record.domain.Learner;
import uk.gov.cslearning.record.dto.BookingDto;

import java.util.Optional;

@Component
public class BookingFactory {
    private final EventFactory eventFactory;
    private final LearnerFactory learnerFactory;

    public BookingFactory(EventFactory eventFactory, LearnerFactory learnerFactory) {
        this.eventFactory = eventFactory;
        this.learnerFactory = learnerFactory;
    }

    public Booking create(BookingDto bookingDto, Optional<Learner> learner) {
        Booking booking = new Booking();

        booking.setBookingTime(bookingDto.getBookingTime());
        booking.setEvent(eventFactory.create(bookingDto.getEvent().getPath()));
        booking.setPaymentDetails(bookingDto.getPaymentDetails().getPath());
        booking.setLearner(learner.isPresent() ? learner.get() : learnerFactory.create(bookingDto.getLearner(), bookingDto.getLearnerEmail()));
        booking.setId(bookingDto.getId());
        booking.setStatus(bookingDto.getStatus().getValue());
        booking.setStatus(bookingDto.getStatus().getValue());

        return booking;
    }
}
