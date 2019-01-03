package uk.gov.cslearning.record.validation.validators;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.dto.InviteDto;
import uk.gov.cslearning.record.service.BookingService;
import uk.gov.cslearning.record.validation.annotations.LearnerNotBooked;
import uk.gov.cslearning.record.validation.annotations.LearnerNotInvited;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.nio.file.Paths;

@Component
public class LearnerNotBookedValidator implements ConstraintValidator<LearnerNotBooked, InviteDto> {

    private final BookingService bookingService;

    public LearnerNotBookedValidator(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    public void initialise(LearnerNotBooked constraint) {}

    @Override
    public boolean isValid(InviteDto invite, ConstraintValidatorContext context) {
        String eventUid = Paths.get(invite.getEvent().getPath()).getFileName().toString();

        return !bookingService.findActiveBookingByEmailAndEvent(invite.getLearnerEmail(), eventUid).isPresent();
    }
}
