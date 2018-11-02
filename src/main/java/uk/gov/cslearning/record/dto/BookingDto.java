package uk.gov.cslearning.record.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingDto {
    private Long id;

    @NotNull(message = "{booking.learner.required}")
    private String learner;

    @NotNull(message = "{booking.event.required}")
    private URI event;
    private BookingStatus status;

    private LocalDateTime bookingTime;

    private URI paymentDetails;
}
