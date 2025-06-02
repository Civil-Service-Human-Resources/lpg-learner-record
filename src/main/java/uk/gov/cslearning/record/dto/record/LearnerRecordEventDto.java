package uk.gov.cslearning.record.dto.record;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class LearnerRecordEventDto {
    private Long learnerRecordId;
    private String resourceId;
    private String learnerId;
    private LearnerRecordEventTypeDto eventType;
    private LearnerRecordEventSourceDto eventSource;
    private Instant eventTimestamp;
}
