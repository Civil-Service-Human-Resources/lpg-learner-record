package uk.gov.cslearning.record.validation.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import uk.gov.cslearning.record.validation.validators.LearnerIsRegisteredValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LearnerIsRegisteredValidator.class)
public @interface LearnerIsRegistered {

    String message() default "Learner not registered.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
