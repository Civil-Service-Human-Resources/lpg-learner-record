package uk.gov.cslearning.record.dto.factory;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.domain.Booking;
import uk.gov.cslearning.record.dto.BookingStatus;
import uk.gov.cslearning.record.dto.BookingDto;

@Component
public class BookingDtoFactory {
    public BookingDto create(Booking booking) {

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setEvent(booking.getEvent().getPath());
        bookingDto.setLearner(booking.getLearner().getUuid());
        bookingDto.setBookingTime(booking.getBookingTime());
        bookingDto.setPaymentDetails(booking.getPaymentDetails());
        bookingDto.setStatus(BookingStatus.forValue(booking.getStatus()));

        return bookingDto;
    }
}
