package uk.gov.cslearning.record.api.record;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LearnerRecordEventQuery {

    List<Integer> eventTypes;
    Integer eventSource;
    String userId;
    Instant before;
    Instant after;

}
