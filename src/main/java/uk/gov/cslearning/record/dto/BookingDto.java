package uk.gov.cslearning.record.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import uk.gov.cslearning.record.validation.annotations.AttendeeNotBooked;
import uk.gov.cslearning.record.validation.annotations.EventIsActive;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Optional;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AttendeeNotBooked
public class BookingDto {
    private Integer id;

    @NotNull(message = "{booking.learner.required}")
    private String learner;

    @NotNull(message = "{booking.learnerEmail.required}")
    private String learnerEmail;

    @NotNull(message = "{booking.learnerName.required}")
    private String learnerName;

    @NotNull(message = "{booking.event.required}")
    @EventIsActive(message = "{booking.event.active}")
    private URI event;

    @NotNull
    private BookingStatus status;

    private Instant bookingTime = Instant.now();

    private Instant confirmationTime;

    private Instant cancellationTime;

    private URI paymentDetails;

    private String poNumber;

    private String bookingReference;

    private String accessibilityOptions;

    private String lineManagerEmail;

    private String lineManagerName;

    private BookingCancellationReason cancellationReason;

    public Optional<String> getEventUid() {
        return getEventPath().map(path -> {
            Path uri = Paths.get(path);
            return uri.getFileName().toString();
        });
    }

    public Optional<String> getEventPath() {
        if (event != null && !event.getPath().isEmpty()) {
            return Optional.of(event.getPath());
        }
        return Optional.empty();
    }
}
