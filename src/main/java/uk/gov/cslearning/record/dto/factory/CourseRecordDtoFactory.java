package uk.gov.cslearning.record.dto.factory;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.dto.CourseRecordDto;

@Component
public class CourseRecordDtoFactory {

    public CourseRecordDto create(CourseRecord courseRecord) {
        CourseRecordDto courseRecordDto = new CourseRecordDto();
        courseRecordDto.setCourseId(courseRecord.getCourseId());
        courseRecordDto.setCourseTitle(courseRecord.getCourseTitle());
        courseRecordDto.setLastUpdated(courseRecord.getLastUpdated());
        courseRecordDto.setLearner(courseRecord.getUserId());
        courseRecordDto.setPreference(courseRecord.getPreference());
        courseRecordDto.setState(String.valueOf(courseRecord.getState()));
        return courseRecordDto;
    }
}
