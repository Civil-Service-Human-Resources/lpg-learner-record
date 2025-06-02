package uk.gov.cslearning.record.dto.record;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.gov.cslearning.record.validation.annotations.LearnerRecordEventId;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@LearnerRecordEventId(groups = {CreateEvent.class})
public class CreateLearnerRecordEventDto {
    private Long learnerRecordId;
    private String resourceId;
    private String learnerId;
    @NotNull
    private String eventType;
    @NotNull
    private String eventSource;
    private LocalDateTime eventTimestamp;
}
