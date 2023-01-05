package uk.gov.cslearning.record.api.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import uk.gov.cslearning.record.domain.CourseRecord;

import java.util.List;

@Data
@AllArgsConstructor
public class CourseRecordOutput {
    public List<CourseRecord> courseRecords;
}
