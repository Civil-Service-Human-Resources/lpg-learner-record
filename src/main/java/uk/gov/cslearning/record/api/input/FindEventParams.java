package uk.gov.cslearning.record.api.input;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FindEventParams {
    private boolean getBookingCount = false;
    private boolean getInvites = false;
    private boolean getBookings = false;
}
