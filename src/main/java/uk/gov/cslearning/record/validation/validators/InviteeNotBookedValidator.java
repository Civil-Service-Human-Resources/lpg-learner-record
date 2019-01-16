package uk.gov.cslearning.record.validation.validators;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.dto.InviteDto;
import uk.gov.cslearning.record.service.BookingService;
import uk.gov.cslearning.record.validation.annotations.InviteeNotBooked;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.nio.file.Paths;

@Component
public class InviteeNotBookedValidator implements ConstraintValidator<InviteeNotBooked, InviteDto> {

    private final BookingService bookingService;

    public InviteeNotBookedValidator(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    public void initialise(InviteeNotBooked constraint) {}

    @Override
    public boolean isValid(InviteDto invite, ConstraintValidatorContext context) {
        String eventUid = Paths.get(invite.getEvent().getPath()).getFileName().toString();

        return !bookingService.findActiveBookingByEmailAndEvent(invite.getLearnerEmail(), eventUid).isPresent();
    }
}
