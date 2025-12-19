package uk.gov.cslearning.record.validation.validators;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.cslearning.record.dto.EventDto;
import uk.gov.cslearning.record.dto.EventStatus;
import uk.gov.cslearning.record.service.EventService;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class EventIsActiveValidatorTest {

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventIsActiveValidator validator;

    @Test
    public void shouldReturnFalseForCancelledEvent() {
        String eventUid = "eventUid-uid";
        EventDto event = new EventDto();
        event.setStatus(EventStatus.CANCELLED);

        when(eventService.findByUid(eventUid, false)).thenReturn(event);

        assertFalse(validator.isValid(eventUid, mock(ConstraintValidatorContext.class)));
    }

    @Test
    public void shouldReturnTrueForActiveEvent() {
        String eventUid = "eventUid-uid";
        EventDto event = new EventDto();
        event.setStatus(EventStatus.ACTIVE);

        when(eventService.findByUid(eventUid, false)).thenReturn(event);

        assertTrue(validator.isValid(eventUid, mock(ConstraintValidatorContext.class)));
    }

    @Test
    public void shouldReturnTrueForMissingEvent() {
        String eventUid = "eventUid-uid";

        when(eventService.findByUid(eventUid, false)).thenReturn(null);

        assertTrue(validator.isValid(eventUid, mock(ConstraintValidatorContext.class)));
    }
}
