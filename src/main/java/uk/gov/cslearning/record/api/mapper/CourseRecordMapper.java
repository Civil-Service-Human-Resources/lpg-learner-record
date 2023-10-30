package uk.gov.cslearning.record.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.cslearning.record.api.input.PATCH.PatchCourseRecordInput;
import uk.gov.cslearning.record.api.input.POST.PostCourseRecordInput;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = ModuleRecordMapper.class)
public abstract class CourseRecordMapper {

    private ModuleRecordMapper moduleRecordMapper;

    @Autowired
    public final void setModuleRecordMapper(ModuleRecordMapper moduleRecordMapper) {
        this.moduleRecordMapper = moduleRecordMapper;
    }

    public abstract PatchCourseRecordInput asInput(CourseRecord courseRecord);

    public abstract void update(@MappingTarget CourseRecord courseRecord, PatchCourseRecordInput input);

    @Mapping(target = "required", source = "isRequired")
    @Mapping(target = "moduleRecords", ignore = true)
    public abstract void updateFromPost(@MappingTarget CourseRecord courseRecord, PostCourseRecordInput input);

    public CourseRecord postInputAsCourseRecord(PostCourseRecordInput inputCourse) {
        CourseRecord cr = new CourseRecord(inputCourse.getCourseId(), inputCourse.getUserId());
        inputCourse.getModuleRecords().forEach(mr -> {
            ModuleRecord convertedModuleRecord = moduleRecordMapper.postInputAsModule(mr);
            cr.addModuleRecord(convertedModuleRecord);
        });
        updateFromPost(cr, inputCourse);
        return cr;
    }

}
