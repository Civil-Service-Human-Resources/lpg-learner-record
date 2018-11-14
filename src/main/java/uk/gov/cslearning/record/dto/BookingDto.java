package uk.gov.cslearning.record.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import uk.gov.cslearning.record.validation.annotations.EventIsActive;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.time.Instant;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingDto {
    private Integer id;

    @NotNull(message = "{booking.learner.required}")
    private String learner;

    @NotNull(message = "{booking.learnerEmail.required}")
    private String learnerEmail;

    @NotNull(message = "{booking.event.required}")
    @EventIsActive(message = "{booking.event.active}")
    private URI event;

    private BookingStatus status;

    private Instant bookingTime;

    private URI paymentDetails;
}
