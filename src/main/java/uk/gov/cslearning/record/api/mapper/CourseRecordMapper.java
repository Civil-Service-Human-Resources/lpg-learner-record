package uk.gov.cslearning.record.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import uk.gov.cslearning.record.api.input.PATCH.PatchCourseRecordInput;
import uk.gov.cslearning.record.domain.CourseRecord;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CourseRecordMapper {

    PatchCourseRecordInput asInput(CourseRecord courseRecord);

    void update(@MappingTarget CourseRecord courseRecord, PatchCourseRecordInput input);
}
