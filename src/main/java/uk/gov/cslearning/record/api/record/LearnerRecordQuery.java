package uk.gov.cslearning.record.api.record;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LearnerRecordQuery {

    List<Integer> learnerRecordTypes;
    String resourceId;
    String userId;
    String uid;

    boolean getChildRecords;

}
