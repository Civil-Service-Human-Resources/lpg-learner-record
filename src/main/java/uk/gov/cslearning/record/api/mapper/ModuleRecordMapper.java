package uk.gov.cslearning.record.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import uk.gov.cslearning.record.api.input.PATCH.PatchModuleRecordInput;
import uk.gov.cslearning.record.api.input.POST.PostModuleRecordInput;
import uk.gov.cslearning.record.domain.ModuleRecord;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ModuleRecordMapper {

    PatchModuleRecordInput asInput(ModuleRecord moduleRecord);

    ModuleRecord PostInputAsModule(PostModuleRecordInput newModule);

    void update(@MappingTarget ModuleRecord moduleRecord, PatchModuleRecordInput input);
}
