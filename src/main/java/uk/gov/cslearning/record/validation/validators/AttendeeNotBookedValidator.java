package uk.gov.cslearning.record.validation.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.dto.BookingDto;
import uk.gov.cslearning.record.service.BookingService;
import uk.gov.cslearning.record.validation.annotations.AttendeeNotBooked;

import java.util.Optional;

@Component
public class AttendeeNotBookedValidator implements ConstraintValidator<AttendeeNotBooked, BookingDto> {

    private final BookingService bookingService;

    public AttendeeNotBookedValidator(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    public void initialise(AttendeeNotBooked constraint) {
    }

    @Override
    public boolean isValid(BookingDto booking, ConstraintValidatorContext context) {
        Optional<String> eventUid = booking.getEventUid();

        return eventUid.filter(s -> bookingService.findActiveBookingByEmailAndEvent(booking.getLearnerEmail(), s).isEmpty()).isPresent();
    }
}
