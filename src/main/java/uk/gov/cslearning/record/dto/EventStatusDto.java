package uk.gov.cslearning.record.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cslearning.record.validation.annotations.EventStatusNotEquals;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventStatusDto {
    @EventStatusNotEquals(value = EventStatus.ACTIVE, message = "{event.status.invalid}")
    private EventStatus status;
}
