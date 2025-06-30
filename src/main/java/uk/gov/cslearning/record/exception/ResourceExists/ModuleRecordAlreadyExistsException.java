package uk.gov.cslearning.record.exception.ResourceExists;

public class ModuleRecordAlreadyExistsException extends ResourceExistsException {

    public ModuleRecordAlreadyExistsException(String moduleId, String userId) {
        super(String.format("Module record already exists for module %s,and user %s", moduleId, userId));
    }
}
