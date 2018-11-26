package uk.gov.cslearning.record.validation.validators;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.cslearning.record.domain.Booking;
import uk.gov.cslearning.record.dto.InviteDto;
import uk.gov.cslearning.record.service.BookingService;

import javax.validation.ConstraintValidatorContext;
import java.net.URI;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LearnerNotBookedValidatorTest {
    @Mock
    private BookingService bookingService;

    private LearnerNotBookedValidator validator;

    @Before
    public void init(){
        MockitoAnnotations.initMocks(this);
        validator = new LearnerNotBookedValidator(bookingService);
    }

    @Test
    public void shouldReturnFalseIfBookingExists() throws Exception{
        InviteDto invite = new InviteDto();
        invite.setEvent(new URI("http://test/path/SRTIBDNE"));
        invite.setLearnerEmail("user@test.com");

        when(bookingService.isLearnerBookedOnEvent("user@test.com", "SRTIBDNE")).thenReturn(Optional.of(new Booking()));

        Assert.assertFalse(validator.isValid(invite, mock(ConstraintValidatorContext.class)));
    }

    @Test
    public void shouldReturnTrueIfBookingDoesNotExist() throws Exception{
        InviteDto invite = new InviteDto();
        invite.setEvent(new URI("http://test/path/SRTIBDNE"));
        invite.setLearnerEmail("user@test.com");

        when(bookingService.isLearnerBookedOnEvent("user@test.com", "SRTIBDNE")).thenReturn(Optional.empty());

        Assert.assertTrue(validator.isValid(invite, mock(ConstraintValidatorContext.class)));
    }
}
