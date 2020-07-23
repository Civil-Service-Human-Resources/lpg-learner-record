package uk.gov.cslearning.record.validation.validators;

import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.domain.State;
import uk.gov.cslearning.record.service.catalogue.Module;

public class CourseStateValidator {
    private CourseStateValidator() {
    }

    public static boolean isCompleted(CourseRecord courseRecord, uk.gov.cslearning.record.service.catalogue.Course catalogueCourse) {
        boolean hasRequired = catalogueCourse.getModules().stream()
            .anyMatch(module -> !module.isOptional());

        for (Module module : catalogueCourse.getModules()) {
            if (!hasRequired || !module.isOptional()) {
                ModuleRecord record = courseRecord.getModuleRecord(module.getId());
                if (record == null || record.getState() != State.COMPLETED) {
                    return false;
                }
            }
        }
        return true;
    }
}
