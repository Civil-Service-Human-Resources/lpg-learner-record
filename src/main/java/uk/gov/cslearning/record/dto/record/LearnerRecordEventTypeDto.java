package uk.gov.cslearning.record.dto.record;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LearnerRecordEventTypeDto {

    private Integer id;
    private String eventType;
    private String description;
    private LearnerRecordTypeDto learnerRecordType;

    public LearnerRecordEventTypeDto(Integer id, String eventType, LearnerRecordTypeDto learnerRecordType) {
        this.id = id;
        this.eventType = eventType;
        this.learnerRecordType = learnerRecordType;
    }
}
