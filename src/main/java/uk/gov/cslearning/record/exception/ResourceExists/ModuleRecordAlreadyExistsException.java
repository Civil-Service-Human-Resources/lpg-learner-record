package uk.gov.cslearning.record.exception.ResourceExists;

public class ModuleRecordAlreadyExistsException extends ResourceExistsException {

    public ModuleRecordAlreadyExistsException(String courseId, String moduleId, String userId) {
        super(String.format("Module record already exists for course %s, module %s and user %s", courseId, moduleId, userId));
    }
}
