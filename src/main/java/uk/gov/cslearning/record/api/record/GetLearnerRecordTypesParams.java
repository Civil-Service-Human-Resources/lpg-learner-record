package uk.gov.cslearning.record.api.record;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetLearnerRecordTypesParams {

    private boolean includeEventTypes = false;

}
