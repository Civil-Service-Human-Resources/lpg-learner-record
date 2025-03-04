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

import java.net.URI;

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
        String eventUid = "event-uid";
        URI eventUri = URI.create("http://example.org/path/to/event/" + eventUid);
        EventDto event = new EventDto();
        event.setStatus(EventStatus.CANCELLED);

        when(eventService.findByUid(eventUid, false)).thenReturn(event);

        assertFalse(validator.isValid(eventUri, mock(ConstraintValidatorContext.class)));
    }

    @Test
    public void shouldReturnTrueForActiveEvent() {
        String eventUid = "event-uid";
        URI eventUri = URI.create("http://example.org/path/to/event/" + eventUid);
        EventDto event = new EventDto();
        event.setStatus(EventStatus.ACTIVE);

        when(eventService.findByUid(eventUid, false)).thenReturn(event);

        assertTrue(validator.isValid(eventUri, mock(ConstraintValidatorContext.class)));
    }

    @Test
    public void shouldReturnTrueForMissingEvent() {
        String eventUid = "event-uid";
        URI eventUri = URI.create("http://example.org/path/to/event/" + eventUid);

        when(eventService.findByUid(eventUid, false)).thenReturn(null);

        assertTrue(validator.isValid(eventUri, mock(ConstraintValidatorContext.class)));
    }
}
