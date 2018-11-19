package uk.gov.cslearning.record.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cslearning.record.validation.annotations.BookingStatusNotEquals;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingStatusDto {

    @BookingStatusNotEquals(value = BookingStatus.REQUESTED, message = "{booking.status.invalid}")
    private BookingStatus status;

    private String cancellationReason;
}
