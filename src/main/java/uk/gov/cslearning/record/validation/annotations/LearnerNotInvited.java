package uk.gov.cslearning.record.validation.annotations;

import uk.gov.cslearning.record.validation.validators.LearnerNotInvitedValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LearnerNotInvitedValidator.class)
public @interface LearnerNotInvited {

    String message() default "Learner not invited.";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
