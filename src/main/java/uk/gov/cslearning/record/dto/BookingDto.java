package uk.gov.cslearning.record.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingDto {
    private Long id;

    @NotBlank(message = "{booking.learner.required}")
    private String learner;

    @NotBlank(message = "{booking.event.required}")
    private String event;
    private BookingStatus status;

    private LocalDateTime bookingTime;
    private String paymentDetails;
}
