package uk.gov.cslearning.record.validation.annotations;

import uk.gov.cslearning.record.validation.validators.EventIsActiveValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EventIsActiveValidator.class)
public @interface EventIsActive {

    String message() default "Event is not active.";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
