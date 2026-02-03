package uk.gov.cslearning.record.validation.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.dto.EventDto;
import uk.gov.cslearning.record.dto.EventStatus;
import uk.gov.cslearning.record.service.EventService;
import uk.gov.cslearning.record.validation.annotations.EventIsActive;

@Component
public class EventIsActiveValidator implements ConstraintValidator<EventIsActive, String> {

    private final EventService eventService;

    public EventIsActiveValidator(EventService eventService) {
        this.eventService = eventService;
    }

    public void initialize(EventIsActive constraint) {
    }

    public boolean isValid(String eventUid, ConstraintValidatorContext context) {
        if (null == eventUid) {
            return true;
        }

        EventDto event = eventService.findByUid(eventUid);
        if (event != null) {
            return event.getStatus().equals(EventStatus.ACTIVE);
        }
        return true;

    }
}
