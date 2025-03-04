package uk.gov.cslearning.record.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import uk.gov.cslearning.record.domain.EventIds;
import uk.gov.cslearning.record.validation.annotations.InviteeNotBooked;
import uk.gov.cslearning.record.validation.annotations.LearnerIsRegistered;
import uk.gov.cslearning.record.validation.annotations.LearnerNotInvited;

import java.net.URI;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@LearnerNotInvited(message = "{invite.learner.notInvited}")
@InviteeNotBooked(message = "{invite.learner.notBooked}")
public class InviteDto {

    private Integer id;

    @NotNull(message = "{invite.event.required}")
    private URI event;

    @NotNull(message = "{invite.learnerEmail.required}")
    @LearnerIsRegistered(message = "{invite.learnerEmail.notRegistered}")
    private String learnerEmail;

    @JsonIgnore
    public EventIds getEventIds() {
        String[] parts = event.getPath().split("/");
        String courseId = parts[parts.length - 5];
        String moduleId = parts[parts.length - 3];
        String eventId = parts[parts.length - 1];
        return new EventIds(courseId, moduleId, eventId);
    }
}
