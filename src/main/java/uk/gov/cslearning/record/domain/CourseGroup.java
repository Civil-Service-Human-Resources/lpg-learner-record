package uk.gov.cslearning.record.domain;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CourseGroup {
    private String userId;
    private Map<String, List<CourseRecord>> courseRecordsGroupedByCourseId;
}
