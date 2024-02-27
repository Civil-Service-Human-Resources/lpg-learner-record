package uk.gov.cslearning.record;

import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.domain.State;

public class TestDataService {

    protected final String courseId = "testCourseId";
    protected final String moduleId = "testModuleId";
    protected final String userId = "testUserId";

    public ModuleRecord generateModuleRecord() {
        ModuleRecord mr = new ModuleRecord();
        mr.setModuleId(moduleId);
        mr.setDuration(100L);
        mr.setState(State.IN_PROGRESS);
        mr.setModuleTitle("Test module title");
        mr.setModuleType("elearning");
        mr.setOptional(false);
        return mr;
    }

    public CourseRecord generateCourseRecord(int numberOfModuleRecords) {
        CourseRecord cr = new CourseRecord(courseId, userId);
        cr.setCourseTitle("Test title");
        cr.setRequired(true);
        for (int i = 0; i < numberOfModuleRecords; i++) {
            ModuleRecord mr = generateModuleRecord();
            mr.setModuleId(moduleId + i);
            mr.setDuration(100L);
            mr.setModuleTitle("Test module title");
            mr.setState(State.IN_PROGRESS);
            mr.setModuleType("elearning");
            mr.setCourseRecord(cr);
            cr.addModuleRecord(mr);
        }
        return cr;
    }
}
