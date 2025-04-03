package uk.gov.cslearning.record.dto.record;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LearnerRecordEventSourceDto {

    private Integer id;
    private String source;
    private String description;

    public LearnerRecordEventSourceDto(Integer id, String source) {
        this.id = id;
        this.source = source;
    }
}
