package uk.gov.cslearning.record.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class CourseRecordServiceTest {

    @InjectMocks
    private CourseRecordService courseRecordService;

    @Test
    public void shouldGetMostRecentlyCompletedForCourse() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String date1 = "2019-01-01 10:30";
        String date2 = "2019-02-01 11:30";
        String date3 = "2019-03-01 12:30";

        ModuleRecord moduleRecord = new ModuleRecord();
        moduleRecord.setCompletionDate(LocalDateTime.parse(date1, formatter));

        ModuleRecord moduleRecord2 = new ModuleRecord();
        moduleRecord2.setCompletionDate(LocalDateTime.parse(date2, formatter));

        ModuleRecord moduleRecord3 = new ModuleRecord();
        moduleRecord3.setCompletionDate(LocalDateTime.parse(date3, formatter));

        CourseRecord courseRecord1 = new CourseRecord("courseid", "userId");
        courseRecord1.setModuleRecords(Arrays.asList(moduleRecord, moduleRecord2, moduleRecord3));

        List<CourseRecord> courseRecordList = Arrays.asList(courseRecord1);
        assertEquals(courseRecordService.getMostRecentlyCompletedForCourse(courseRecordList), LocalDateTime.parse(date3, formatter).toLocalDate());
    }
}