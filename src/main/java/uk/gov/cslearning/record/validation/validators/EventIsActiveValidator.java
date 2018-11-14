package uk.gov.cslearning.record.validation.validators;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.dto.EventDto;
import uk.gov.cslearning.record.dto.EventStatus;
import uk.gov.cslearning.record.service.EventService;
import uk.gov.cslearning.record.validation.annotations.EventIsActive;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.net.URI;
import java.nio.file.Paths;
import java.util.Optional;

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

       return eventService.findByUid(eventUid)
              .map(eventDto -> eventDto.getStatus().equals(EventStatus.ACTIVE))
              .orElse(true);
   }
}
