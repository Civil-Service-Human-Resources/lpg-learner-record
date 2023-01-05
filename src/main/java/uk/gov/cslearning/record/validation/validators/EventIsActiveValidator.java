package uk.gov.cslearning.record.validation.validators;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.dto.EventDto;
import uk.gov.cslearning.record.dto.EventStatus;
import uk.gov.cslearning.record.service.EventService;
import uk.gov.cslearning.record.validation.annotations.EventIsActive;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.net.URI;
import java.nio.file.Paths;

@Component
public class EventIsActiveValidator implements ConstraintValidator<EventIsActive, URI> {

   private final EventService eventService;

   public EventIsActiveValidator(EventService eventService) {
      this.eventService = eventService;
   }

   public void initialize(EventIsActive constraint) {
   }

   public boolean isValid(URI eventUri, ConstraintValidatorContext context) {
       if (null == eventUri) {
           return true;
       }

       String eventUid = Paths.get(eventUri.getPath()).getFileName().toString();
       EventDto event = eventService.findByUid(eventUid, false);
       if (event != null) {
           return event.getStatus().equals(EventStatus.ACTIVE);
       }
       return true;

   }
}
