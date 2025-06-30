package uk.gov.cslearning.record.validation.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import uk.gov.cslearning.record.validation.validators.LearnerRecordEventIdValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LearnerRecordEventIdValidator.class)
public @interface LearnerRecordEventId {

    String message() default "Either [resourceId, learnerId] or [learnerRecordId] must be provided when creating a learner record event.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
