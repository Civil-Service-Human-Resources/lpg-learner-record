package uk.gov.cslearning.record.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.dto.CourseRecordDto;
import uk.gov.cslearning.record.repository.CourseRecordRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseRecordService {

    private final CourseRecordRepository courseRecordRepository;

    public CourseRecordService(CourseRecordRepository courseRecordRepository) {
        this.courseRecordRepository = courseRecordRepository;
    }

    @Transactional(readOnly = true)
    public List<CourseRecordDto> listRecordsForPeriod(LocalDate periodStart, LocalDate periodEnd) {

        List<CourseRecord> courseRecords = courseRecordRepository
                .findAllByLastUpdatedBetween(periodStart.atStartOfDay(), periodEnd.plusDays(1).atStartOfDay()).stream()
                .collect(Collectors.toList());

        List<CourseRecordDto> result = new ArrayList<>();

        for (CourseRecord courseRecord : courseRecords) {
            CourseRecordDto courseRecordDto = new CourseRecordDto();
            result.add(courseRecordDto);

            courseRecordDto.setCourseId(courseRecord.getCourseId());
            courseRecordDto.setCourseTitle(courseRecord.getCourseTitle());
            courseRecordDto.setLastUpdated(courseRecord.getLastUpdated());
            courseRecordDto.setLearner(courseRecord.getUserId());
            courseRecordDto.setPreference(courseRecord.getPreference());
            courseRecordDto.setState(String.valueOf(courseRecord.getState()));
        }

        return result;
    }
}
