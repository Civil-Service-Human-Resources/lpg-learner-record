package uk.gov.cslearning.record.validation.annotations;

import uk.gov.cslearning.record.dto.BookingStatus;
import uk.gov.cslearning.record.validation.validators.BookingStatusEqualsValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy= BookingStatusEqualsValidator.class)
public @interface BookingStatusEquals {
    BookingStatus value();

    String message() default "";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}