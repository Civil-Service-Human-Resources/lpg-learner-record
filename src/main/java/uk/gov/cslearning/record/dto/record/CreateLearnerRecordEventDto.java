package uk.gov.cslearning.record.dto.record;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class CreateLearnerRecordEventDto {
    @NotNull
    private Integer eventType;
    @NotNull
    private Integer eventSource;
    private LocalDateTime eventTimestamp;
    @NotNull(groups = {CreateEvent.class})
    private Long learnerRecordId;
}
