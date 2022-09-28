package uk.gov.cslearning.record.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.cslearning.record.api.input.PATCH.PatchCourseRecordInput;
import uk.gov.cslearning.record.api.input.POST.PostCourseRecordInput;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;

import javax.inject.Inject;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = ModuleRecordMapper.class)
public abstract class CourseRecordMapper {

    @Autowired
    private ModuleRecordMapper moduleRecordMapper;

    public abstract PatchCourseRecordInput asInput(CourseRecord courseRecord);

    public abstract void update(@MappingTarget CourseRecord courseRecord, PatchCourseRecordInput input);

    @Mapping(target = "moduleRecords", ignore = true)
    public abstract void updateFromPost(@MappingTarget CourseRecord courseRecord, PostCourseRecordInput input);

    public CourseRecord postInputAsCourseRecord(PostCourseRecordInput inputCourse) {
        LocalDateTime now = LocalDateTime.now();
        CourseRecord cr = new CourseRecord(inputCourse.getCourseId(), inputCourse.getUserId());
        cr.setLastUpdated(now);
        inputCourse.getModuleRecords().forEach(mr -> {
            ModuleRecord convertedModuleRecord = moduleRecordMapper.postInputAsModule(mr);
            convertedModuleRecord.setCreatedAt(now);
            convertedModuleRecord.setUpdatedAt(now);
            cr.addModuleRecord(convertedModuleRecord);
        });
        updateFromPost(cr, inputCourse);
        return cr;
    }

}
