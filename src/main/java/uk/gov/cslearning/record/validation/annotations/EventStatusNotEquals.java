package uk.gov.cslearning.record.validation.annotations;

import uk.gov.cslearning.record.dto.EventStatus;
import uk.gov.cslearning.record.validation.validators.EventStatusNotEqualsValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EventStatusNotEqualsValidator.class)
public @interface EventStatusNotEquals {
    EventStatus value();

    String message() default "";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}