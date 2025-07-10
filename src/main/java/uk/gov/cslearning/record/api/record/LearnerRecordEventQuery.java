package uk.gov.cslearning.record.api.record;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LearnerRecordEventQuery {

    List<String> eventTypes;
    Integer eventSource;
    String userId;
    List<String> resourceIds;
    LocalDateTime before;
    LocalDateTime after;

}
