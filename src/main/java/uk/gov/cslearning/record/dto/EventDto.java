package uk.gov.cslearning.record.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Collection;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventDto {
    private Integer id;
    private String uid;
    private EventStatus status;
    private CancellationReason cancellationReason;
    private Integer activeBookingCount;
    private Collection<BookingDto> activeBookings;
    private Collection<InviteDto> invites;
}
