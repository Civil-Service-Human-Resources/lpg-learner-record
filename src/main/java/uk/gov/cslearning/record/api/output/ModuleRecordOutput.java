package uk.gov.cslearning.record.api.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import uk.gov.cslearning.record.domain.ModuleRecord;

import java.util.List;

@Data
@AllArgsConstructor
public class ModuleRecordOutput {
    public List<ModuleRecord> moduleRecords;
}
