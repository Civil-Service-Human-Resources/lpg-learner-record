package uk.gov.cslearning.record.api.output.error;

public class GenericErrorResponse {

    private int status;
    private String code;
    private String message;

    public GenericErrorResponse(int status, String code, Exception ex) {
        this.status = status;
        this.code = code;
        this.message = ex.getMessage();
    }
}
