package uk.gov.cslearning.record.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import uk.gov.cslearning.record.domain.BookingStatus;

import java.net.URI;
import java.time.Instant;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingDto {
    private Integer id;

    @NotNull(message = "{booking.learner.required}")
    private String learner;

    private String eventUid;

    @NotNull
    private BookingStatus status;

    private Instant bookingTime;

    private Instant confirmationTime;

    private Instant cancellationTime;

    private URI paymentDetails;

    private String poNumber;

    private String bookingReference;

    private String accessibilityOptions;

    private BookingCancellationReason cancellationReason;

}
