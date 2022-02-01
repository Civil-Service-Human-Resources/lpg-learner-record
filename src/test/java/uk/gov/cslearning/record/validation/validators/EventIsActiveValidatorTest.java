package uk.gov.cslearning.record.validation.validators;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.record.dto.EventDto;
import uk.gov.cslearning.record.dto.EventStatus;
import uk.gov.cslearning.record.service.EventService;

import javax.validation.ConstraintValidatorContext;
import java.net.URI;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
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

        when(eventService.findByUid(eventUid)).thenReturn(event);

        assertFalse(validator.isValid(eventUri, mock(ConstraintValidatorContext.class)));
    }

    @Test
    public void shouldReturnTrueForActiveEvent() {
        String eventUid = "event-uid";
        URI eventUri = URI.create("http://example.org/path/to/event/" + eventUid);
        EventDto event = new EventDto();
        event.setStatus(EventStatus.ACTIVE);

        when(eventService.findByUid(eventUid)).thenReturn(event);

        assertTrue(validator.isValid(eventUri, mock(ConstraintValidatorContext.class)));
    }

    @Test
    public void shouldReturnTrueForMissingEvent() {
        String eventUid = "event-uid";
        URI eventUri = URI.create("http://example.org/path/to/event/" + eventUid);

        when(eventService.findByUid(eventUid)).thenReturn(null);

        assertTrue(validator.isValid(eventUri, mock(ConstraintValidatorContext.class)));
    }
}
