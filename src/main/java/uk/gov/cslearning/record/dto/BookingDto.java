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

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AttendeeNotBooked
public class BookingDto {
    private Integer id;

    @NotNull(message = "{booking.learner.required}")
    private String learner;

    @NotNull(message = "{booking.learnerEmail.required}")
    private String learnerEmail;

    @NotNull(message = "{booking.event.required}")
    @EventIsActive(message = "{booking.event.active}")
    private URI event;

    @NotNull
    private BookingStatus status;

    private Instant bookingTime = Instant.now();

    private URI paymentDetails;

    private String poNumber;

    public String getEventUid(){
        String path = getEventPath();

        if (path != null && !path.equals("")) {
            Path uri = Paths.get(path);
            return uri.getFileName().toString();
        }
        return null;
    }

    public String getEventPath() {
        if(event != null && event.getPath() != null) {
            return event.getPath();
        }
        return null;
    }
}
