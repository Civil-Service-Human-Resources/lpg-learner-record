package uk.gov.cslearning.record.validation.validators;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.cslearning.record.service.identity.Identity;
import uk.gov.cslearning.record.service.identity.IdentityService;

import javax.validation.ConstraintValidatorContext;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LearnerIsRegisteredValidatorTest {
    @Mock
    private IdentityService identityService;

    private LearnerIsRegisteredValidator validator;

    @Before
    public void init(){
        MockitoAnnotations.initMocks(this);
        validator = new LearnerIsRegisteredValidator(identityService);
    }

    @Test
    public void shouldReturnTrueIfBookingExists(){
        when(identityService.getIdentityByEmailAddress("user@test.com")).thenReturn(Optional.of(new Identity()));

        Assert.assertTrue(validator.isValid("user@test.com", mock(ConstraintValidatorContext.class)));
    }

    @Test
    public void shouldReturnFalseIfBookingDoesNotExist(){
        when(identityService.getIdentityByEmailAddress("user@test.com")).thenReturn(Optional.empty());

        Assert.assertFalse(validator.isValid("user@test.com", mock(ConstraintValidatorContext.class)));
    }
}
