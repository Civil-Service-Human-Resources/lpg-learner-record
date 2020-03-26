package uk.gov.cslearning.record.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.dto.CourseRecordDto;
import uk.gov.cslearning.record.repository.CourseRecordRepository;
import uk.gov.cslearning.record.dto.factory.CourseRecordDtoFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseRecordService {

    private final CourseRecordRepository courseRecordRepository;
    private final CourseRecordDtoFactory courseRecordDtoFactory;

    public CourseRecordService(CourseRecordRepository courseRecordRepository, CourseRecordDtoFactory courseRecordDtoFactory) {
        this.courseRecordRepository = courseRecordRepository;
        this.courseRecordDtoFactory = courseRecordDtoFactory;
    }

    @Transactional(readOnly = true)
    public List<CourseRecordDto> listRecordsForPeriod(LocalDate periodStart, LocalDate periodEnd) {

        List<CourseRecord> courseRecords = courseRecordRepository
                .findAllByLastUpdatedBetween(periodStart.atStartOfDay(), periodEnd.plusDays(1).atStartOfDay()).stream()
                .collect(Collectors.toList());
        return courseRecordDtoFactory.create(courseRecords);
    }
}
