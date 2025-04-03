package uk.gov.cslearning.record.dto.record;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LearnerRecordTypeDto {

    private Integer id;
    private String type;
    private List<LearnerRecordEventTypeDto> validEventTypes;

    public LearnerRecordTypeDto(Integer id, String type) {
        this.id = id;
        this.type = type;
        this.validEventTypes = new ArrayList<>();
    }
}
