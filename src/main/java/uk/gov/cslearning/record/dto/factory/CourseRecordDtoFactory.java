package uk.gov.cslearning.record.dto.factory;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.dto.CourseRecordDto;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CourseRecordDtoFactory {
    public List<CourseRecordDto> create(List<CourseRecord> courseRecords) {
        return courseRecords
                .stream()
                .map(record -> {
                    CourseRecordDto courseRecordDto = new CourseRecordDto();
                    courseRecordDto.setCourseId(record.getCourseId());
                    courseRecordDto.setCourseTitle(record.getCourseTitle());
                    courseRecordDto.setLastUpdated(record.getLastUpdated());
                    courseRecordDto.setLearner(record.getUserId());
                    courseRecordDto.setPreference(record.getPreference());
                    courseRecordDto.setState(String.valueOf(record.getState()));
                    return courseRecordDto;
                })
                .collect(Collectors.toList());
    }
}
