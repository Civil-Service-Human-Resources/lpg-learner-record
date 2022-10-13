package uk.gov.cslearning.record.exception;

public class ModuleRecordNotFoundException extends RuntimeException {
    public ModuleRecordNotFoundException(Long moduleRecordId) {
        super(String.format("Module record does not exist with  id: %s", moduleRecordId));
    }
}
