package uk.gov.cslearning.record.api.record;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetLearnerRecordTypesParams {

    private boolean includeEventTypes = false;

}
