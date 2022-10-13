package uk.gov.cslearning.record.api.output.error;

import lombok.Data;

import java.util.List;

@Data
public class GenericErrorResponse {

    private int status;
    private String code;
    private List<String> messages;

    public GenericErrorResponse(int status, String code, List<String> messages) {
        this.status = status;
        this.code = code;
        this.messages = messages;
    }
}
