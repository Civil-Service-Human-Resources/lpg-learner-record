package uk.gov.cslearning.record.validation.validators;

import uk.gov.cslearning.record.api.input.POST.PostCourseRecordInput;
import uk.gov.cslearning.record.api.input.POST.PostModuleRecordInput;
import uk.gov.cslearning.record.validation.annotations.LearnerIsRegistered;
import uk.gov.cslearning.record.validation.annotations.ModuleRecordMatchesCourseRecord;
import uk.gov.cslearning.record.validation.annotations.ValidEnum;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModuleRecordMatchesCourseRecordValidator implements ConstraintValidator<ModuleRecordMatchesCourseRecord, PostCourseRecordInput> {

    @Override
    public boolean isValid(PostCourseRecordInput value, ConstraintValidatorContext context) {
        PostModuleRecordInput moduleRecord = value.getModuleRecords().get(0);

        return moduleRecord.getCourseId().equals(value.getCourseId()) && moduleRecord.getUserId().equals((value.getUserId()));
    }

}
