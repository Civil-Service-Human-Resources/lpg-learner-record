package uk.gov.cslearning.record.validation.validators;

import uk.gov.cslearning.record.api.input.POST.PostCourseRecordInput;
import uk.gov.cslearning.record.api.input.POST.PostModuleRecordInput;
import uk.gov.cslearning.record.validation.annotations.LearnerIsRegistered;
import uk.gov.cslearning.record.validation.annotations.ModuleRecordMatchesCourseRecord;
import uk.gov.cslearning.record.validation.annotations.ValidEnum;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModuleRecordMatchesCourseRecordValidator implements ConstraintValidator<ModuleRecordMatchesCourseRecord, PostCourseRecordInput> {

    @Override
    public boolean isValid(PostCourseRecordInput value, ConstraintValidatorContext context) {
        boolean valid = true;
        List<PostModuleRecordInput> modules = value.getModuleRecords();
        if (modules.size() > 0) {
            List<PostModuleRecordInput> validRecords = modules.stream().filter(m -> doesUserIdAndCourseIdMatch(m, value)).collect(Collectors.toList());
            valid = (validRecords.size() == modules.size());
        }
        return valid;
    }

    private boolean doesUserIdAndCourseIdMatch(PostModuleRecordInput moduleRecord, PostCourseRecordInput courseRecord) {
        return moduleRecord.getCourseId().equals(courseRecord.getCourseId()) && moduleRecord.getUserId().equals((courseRecord.getUserId()));
    }

}
