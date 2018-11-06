package uk.gov.cslearning.record.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cslearning.record.validation.annotations.BookingStatusEquals;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingStatusDto {

    @BookingStatusEquals(value = BookingStatus.CONFIRMED, message = "{booking.status.confirmed}")
    private BookingStatus status;
}
