package uk.gov.cslearning.record.api.record;

import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cslearning.record.dto.record.CourseRecordController;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LearnerRecordQuery {

    @Null(groups = {CourseRecordController.class})
    List<Integer> learnerRecordTypes;
    String resourceId;
    String learnerId;
    String uid;

    boolean getChildRecords;

}
