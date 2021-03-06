package uk.gov.cslearning.record.validation.validators;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.dto.BookingDto;
import uk.gov.cslearning.record.dto.InviteDto;
import uk.gov.cslearning.record.service.BookingService;
import uk.gov.cslearning.record.validation.annotations.AttendeeNotBooked;
import uk.gov.cslearning.record.validation.annotations.LearnerNotInvited;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.nio.file.Paths;
import java.util.Optional;

@Component
public class AttendeeNotBookedValidator implements ConstraintValidator<LearnerNotInvited, BookingDto> {

    private final BookingService bookingService;

    public AttendeeNotBookedValidator(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    public void initialise(AttendeeNotBooked constraint) {}

    @Override
    public boolean isValid(BookingDto booking, ConstraintValidatorContext context) {
        Optional<String> eventUid = booking.getEventUid();

        if (eventUid.isPresent()) {
            return !bookingService.findActiveBookingByEmailAndEvent(booking.getLearnerEmail(), eventUid.get()).isPresent();
        }
        return false;
    }
}
