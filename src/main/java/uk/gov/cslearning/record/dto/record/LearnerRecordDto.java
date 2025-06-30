package uk.gov.cslearning.record.dto.record;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
public class LearnerRecordDto {
    private Long id;
    private String uid;
    private LearnerRecordTypeDto recordType;
    private Long parentId;
    private String resourceId;
    private Instant createdTimestamp;
    private String learnerId;
    private List<LearnerRecordDto> children;
    private List<LearnerRecordEventDto> events;
    private Integer eventCount;
    private LearnerRecordEventDto latestEvent;

    public LearnerRecordDto(Long id, String uid, LearnerRecordTypeDto recordType, Long parentId, String resourceId,
                            Instant createdTimestamp, String learnerId, Integer eventCount, LearnerRecordEventDto latestEvent) {
        this(id, uid, recordType, parentId, resourceId, createdTimestamp, learnerId, null, null, eventCount, latestEvent);
    }

}
