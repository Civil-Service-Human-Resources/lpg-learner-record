package uk.gov.cslearning.record.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InviteDto {

    private Integer id;

    @NotNull(message = "{invite.learnerEmail.required}")
    private String learnerEmail;

    @NotNull(message = "{invite.learnerEmail.required}")
    private String learnerUid;

}
