package uk.gov.cslearning.record.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.dto.CourseRecordDto;
import uk.gov.cslearning.record.dto.factory.CourseRecordDtoFactory;
import uk.gov.cslearning.record.repository.CourseRecordRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CourseRecordServiceTest {

    @Mock
    private CourseRecordRepository courseRecordRepository;

    @Mock
    private CourseRecordDtoFactory courseRecordDtoFactory;

    @InjectMocks
    private CourseRecordService courseRecordService;

    @Test
    public void shouldReturnListOfCourseRecord() {
        LocalDate from = LocalDate.now().minusDays(7);
        LocalDate to = LocalDate.now();

        List<CourseRecord> courseRecordList = new ArrayList<>();
        CourseRecord courseRecord1 = new CourseRecord();
        CourseRecord courseRecord2 = new CourseRecord();
        courseRecordList.add(courseRecord1);
        courseRecordList.add(courseRecord2);

        CourseRecordDto dto1 = new CourseRecordDto();
        CourseRecordDto dto2 = new CourseRecordDto();

        when(courseRecordRepository.findAllByLastUpdatedBetween(from.atStartOfDay(), to.plusDays(1).atStartOfDay()))
                .thenReturn(courseRecordList);
        when(courseRecordDtoFactory.create(courseRecord1)).thenReturn(dto1);
        when(courseRecordDtoFactory.create(courseRecord2)).thenReturn(dto2);

        assertEquals(courseRecordList.size(),
                courseRecordService.listRecordsForPeriod(from, to).size());
    }
}