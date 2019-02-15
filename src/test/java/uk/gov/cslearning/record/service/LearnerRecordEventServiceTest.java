package uk.gov.cslearning.record.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.dto.LearnerRecordEvent;
import uk.gov.cslearning.record.dto.factory.LearnerRecordEventFactory;
import uk.gov.cslearning.record.repository.CourseRecordRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LearnerRecordEventServiceTest {

    @Mock
    private CourseRecordRepository courseRecordRepository;

    @Mock
    private LearnerRecordEventFactory learnerRecordEventFactory;

    @InjectMocks
    private LearnerRecordEventService learnerRecordEventService;

    @Test
    public void shouldReturnListOfLearnerRecordEvents() {
        String userId = "user-id";
        String courseId = "course-id";
        String moduleId = "module-id";

        ModuleRecord moduleRecord = new ModuleRecord(moduleId);
        moduleRecord.setEventDate(LocalDate.now().plusDays(1));
        CourseRecord courseRecord = new CourseRecord(courseId, userId);
        courseRecord.addModuleRecord(moduleRecord);

        when(courseRecordRepository.listEventRecords()).thenReturn(Collections.singletonList(courseRecord));
        LearnerRecordEvent learnerRecordEvent = new LearnerRecordEvent();
        when(learnerRecordEventFactory.create(courseRecord, moduleRecord)).thenReturn(learnerRecordEvent);

        Collection<LearnerRecordEvent> courseRecords = learnerRecordEventService.listEvents();
        assertEquals(Collections.singletonList(learnerRecordEvent), courseRecords);
    }
}