package uk.gov.cslearning.record.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import uk.gov.cslearning.record.api.input.CourseRecordInput;
import uk.gov.cslearning.record.domain.CourseRecord;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CourseRecordMapper {

    CourseRecordInput asInput(CourseRecord courseRecord);

    void update(@MappingTarget CourseRecord courseRecord, CourseRecordInput input);
}
