package uk.gov.cslearning.record.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import uk.gov.cslearning.record.validation.annotations.LearnerNotBooked;
import uk.gov.cslearning.record.validation.annotations.LearnerNotInvited;

import javax.validation.constraints.NotNull;
import java.net.URI;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@LearnerNotInvited
@LearnerNotBooked
public class InviteDto {

    private Integer id;

    @NotNull(message = "{invite.event.required}")
    private URI event;

    @NotNull(message = "{invite.learnerEmail.required")
    private String learnerEmail;
}
