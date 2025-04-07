package uk.gov.cslearning.record.dto.record;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class LearnerRecordEventDto {
    private Long id;
    private Long learnerRecordId;
    private LearnerRecordEventTypeDto eventType;
    private LearnerRecordEventSourceDto eventSource;
    private Instant eventTimestamp;
}
