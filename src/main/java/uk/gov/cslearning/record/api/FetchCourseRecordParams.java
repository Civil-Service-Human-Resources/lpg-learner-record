package uk.gov.cslearning.record.api;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FetchCourseRecordParams {
    @NotNull
    List<String> userIds;
    List<String> courseIds;
}
