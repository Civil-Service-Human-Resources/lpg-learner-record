package uk.gov.cslearning.record.exception;

public class CivilServantNotFoundException extends RuntimeException {
    public CivilServantNotFoundException(String uid) {
        super(String.format("Civil Servant does not exist with uid: %s", uid));
    }
}
