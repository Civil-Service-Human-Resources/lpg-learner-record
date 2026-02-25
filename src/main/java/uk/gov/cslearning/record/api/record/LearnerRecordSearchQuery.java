package uk.gov.cslearning.record.api.record;

import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uk.gov.cslearning.record.dto.record.CourseRecordController;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class LearnerRecordSearchQuery {

    @Null(groups = {CourseRecordController.class})
    List<String> learnerRecordTypes;
    List<String> learnerIds;

    LocalDateTime createdTimestampGte;
    LocalDateTime updatedTimestampGte;

}
