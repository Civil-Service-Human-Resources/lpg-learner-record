package uk.gov.cslearning.record.validation.validators;


import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.cslearning.record.domain.Booking;
import uk.gov.cslearning.record.dto.InviteDto;
import uk.gov.cslearning.record.service.BookingService;

import java.net.URI;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class InviteeNotBookedValidatorTest {
    @Mock
    private BookingService bookingService;

    @InjectMocks
    private InviteeNotBookedValidator validator;

    @Test
    public void shouldReturnFalseIfBookingExists() throws Exception {
        InviteDto invite = new InviteDto();
        invite.setEvent(new URI("http://test/path/SRTIBDNE"));
        invite.setLearnerEmail("user@test.com");

        when(bookingService.findActiveBookingByEmailAndEvent("user@test.com", "SRTIBDNE")).thenReturn(Optional.of(new Booking()));

        assertFalse(validator.isValid(invite, mock(ConstraintValidatorContext.class)));
    }

    @Test
    public void shouldReturnTrueIfBookingDoesNotExist() throws Exception {
        InviteDto invite = new InviteDto();
        invite.setEvent(new URI("http://test/path/SRTIBDNE"));
        invite.setLearnerEmail("user@test.com");

        when(bookingService.findActiveBookingByEmailAndEvent("user@test.com", "SRTIBDNE")).thenReturn(Optional.empty());

        assertTrue(validator.isValid(invite, mock(ConstraintValidatorContext.class)));
    }
}
