package uk.gov.cslearning.record.validation.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import uk.gov.cslearning.record.validation.validators.AttendeeNotBookedValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AttendeeNotBookedValidator.class)
public @interface AttendeeNotBooked {

    String message() default "Learner not booked.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
