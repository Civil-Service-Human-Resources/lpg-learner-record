package uk.gov.cslearning.record.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.cslearning.record.api.input.PATCH.PatchModuleRecordInput;
import uk.gov.cslearning.record.api.input.POST.PostCourseRecordInput;
import uk.gov.cslearning.record.api.input.POST.PostModuleRecordInput;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.domain.State;
import uk.gov.cslearning.record.service.catalogue.Module;

import java.time.Clock;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ModuleRecordMapper {

    private Clock clock;

    @Autowired
    public final void setClock(Clock clock) {
        this.clock = clock;
    }

    public abstract PatchModuleRecordInput asInput(ModuleRecord moduleRecord);

    public abstract void updateFromPost(@MappingTarget ModuleRecord moduleRecord, PostModuleRecordInput input);

    public ModuleRecord postInputAsModule(PostModuleRecordInput newModule) {
        LocalDateTime now = LocalDateTime.now(clock);
        ModuleRecord mr = new ModuleRecord();
        mr.setCreatedAt(now);
        mr.setUpdatedAt(now);
        if (newModule.getState().equals(State.COMPLETED.toString())) {
            mr.setCompletionDate(now);
        }
        updateFromPost(mr, newModule);
        return mr;
    }

    public abstract void update(@MappingTarget ModuleRecord moduleRecord, PatchModuleRecordInput input);
}
