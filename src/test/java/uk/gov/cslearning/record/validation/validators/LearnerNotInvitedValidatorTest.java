package uk.gov.cslearning.record.validation.validators;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.cslearning.record.dto.InviteDto;
import uk.gov.cslearning.record.service.InviteService;

import javax.validation.ConstraintValidatorContext;
import java.net.URI;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LearnerNotInvitedValidatorTest {
    @Mock
    private InviteService inviteService;

    private LearnerNotInvitedValidator validator;

    @Before
    public void init(){
        MockitoAnnotations.initMocks(this);
        validator = new LearnerNotInvitedValidator(inviteService);
    }

    @Test
    public void shouldReturnFalseIfBookingExists() throws Exception{
        InviteDto invite = new InviteDto();
        invite.setEvent(new URI("http://test/path/SRTIBDNE"));
        invite.setLearnerEmail("user@test.com");

        when(inviteService.findByEventIdAndLearnerEmail("SRTIBDNE", "user@test.com")).thenReturn(Optional.of(new InviteDto()));

        Assert.assertFalse(validator.isValid(invite, mock(ConstraintValidatorContext.class)));
    }

    @Test
    public void shouldReturnTrueIfBookingDoesNotExist() throws Exception{
        InviteDto invite = new InviteDto();
        invite.setEvent(new URI("http://test/path/SRTIBDNE"));
        invite.setLearnerEmail("user@test.com");

        when(inviteService.findByEventIdAndLearnerEmail("SRTIBDNE", "user@test.com")).thenReturn(Optional.empty());

        Assert.assertTrue(validator.isValid(invite, mock(ConstraintValidatorContext.class)));
    }
}
