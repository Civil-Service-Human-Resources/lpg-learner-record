package uk.gov.cslearning.record.validation.annotations;

import uk.gov.cslearning.record.validation.validators.LearnerNotInvitedValidator;
import uk.gov.cslearning.record.validation.validators.ModuleRecordMatchesCourseRecordValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ModuleRecordMatchesCourseRecordValidator.class)
public @interface ModuleRecordMatchesCourseRecord {

    String message() default "Module record details and course record details do not match";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
