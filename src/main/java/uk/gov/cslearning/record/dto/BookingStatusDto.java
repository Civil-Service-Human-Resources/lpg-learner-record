package uk.gov.cslearning.record.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingStatusDto {

    private BookingStatus status;
}
