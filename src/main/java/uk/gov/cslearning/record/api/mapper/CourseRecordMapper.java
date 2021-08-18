package uk.gov.cslearning.record.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import uk.gov.cslearning.record.api.input.CourseRecordInput;
import uk.gov.cslearning.record.domain.CourseRecord;

@Mapper
public interface CourseRecordMapper {

    CourseRecord asCourseRecord(CourseRecordInput input);

    CourseRecordInput asInput(CourseRecord courseRecord);

    void update(@MappingTarget CourseRecord courseRecord, CourseRecordInput input);
}
