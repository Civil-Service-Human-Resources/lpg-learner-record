package uk.gov.cslearning.record.validation.validators;


import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.cslearning.record.domain.identity.Identity;
import uk.gov.cslearning.record.service.identity.IdentitiesService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class LearnerIsRegisteredValidatorTest {
    @Mock
    private IdentitiesService identityService;

    @InjectMocks
    private LearnerIsRegisteredValidator validator;

    @Test
    public void shouldReturnTrueIfBookingExists() {
        Identity i = new Identity("", "", List.of());
        when(identityService.getIdentityByEmailAddress("user@test.com")).thenReturn(Optional.of(i));

        assertTrue(validator.isValid("user@test.com", mock(ConstraintValidatorContext.class)));
    }

    @Test
    public void shouldReturnFalseIfBookingDoesNotExist() {
        when(identityService.getIdentityByEmailAddress("user@test.com")).thenReturn(Optional.empty());

        assertFalse(validator.isValid("user@test.com", mock(ConstraintValidatorContext.class)));
    }
}
