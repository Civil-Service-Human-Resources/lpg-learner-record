package uk.gov.cslearning.record.validation.annotations;

import uk.gov.cslearning.record.validation.validators.LearnerIsRegisteredValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LearnerIsRegisteredValidator.class)
public @interface LearnerIsRegistered {

    String message() default "Learner not registered.";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
