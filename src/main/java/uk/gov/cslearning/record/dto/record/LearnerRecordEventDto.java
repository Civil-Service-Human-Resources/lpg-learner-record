package uk.gov.cslearning.record.dto.record;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class LearnerRecordEventDto {
    private Long id;
    private Long learnerRecordId;
    private Integer eventType;
    private Integer eventSource;
    private Instant eventTimestamp;
}
