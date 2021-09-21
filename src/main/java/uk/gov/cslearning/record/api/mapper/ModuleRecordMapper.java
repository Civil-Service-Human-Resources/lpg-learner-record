package uk.gov.cslearning.record.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import uk.gov.cslearning.record.api.input.PATCH.PatchModuleRecordInput;
import uk.gov.cslearning.record.api.input.POST.PostCourseRecordInput;
import uk.gov.cslearning.record.api.input.POST.PostModuleRecordInput;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.service.catalogue.Module;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ModuleRecordMapper {

    PatchModuleRecordInput asInput(ModuleRecord moduleRecord);

    ModuleRecord postInputAsModuleBasic(PostModuleRecordInput newModule);

    void updateFromPost(@MappingTarget ModuleRecord moduleRecord, PostModuleRecordInput input);

    default ModuleRecord postInputAsModule(PostModuleRecordInput newModule) {
        LocalDateTime now = LocalDateTime.now();
        ModuleRecord mr = new ModuleRecord();
        mr.setCreatedAt(now);
        mr.setUpdatedAt(now);
        updateFromPost(mr, newModule);
        return mr;
    }

    void update(@MappingTarget ModuleRecord moduleRecord, PatchModuleRecordInput input);
}
