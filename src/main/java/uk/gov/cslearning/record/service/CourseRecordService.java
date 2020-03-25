package uk.gov.cslearning.record.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.dto.CourseRecordDto;
import uk.gov.cslearning.record.repository.CourseRecordRepository;

import java.time.LocalDate;
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
        return courseRecordRepository
                .findAllByCreatedAtBetweenAndCourseRecordIsNotNullNormalised(periodStart.atStartOfDay(), periodEnd.plusDays(1).atStartOfDay()).stream()
                .collect(Collectors.toList());
    }
}
