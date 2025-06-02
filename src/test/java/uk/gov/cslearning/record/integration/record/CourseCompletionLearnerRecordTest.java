package uk.gov.cslearning.record.integration.record;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.IntegrationTestBase;
import uk.gov.cslearning.record.TestDataService;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.CourseRecordIdentity;
import uk.gov.cslearning.record.domain.State;
import uk.gov.cslearning.record.repository.CourseRecordRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.cslearning.record.TestUtils.assertTime;

public class CourseCompletionLearnerRecordTest extends IntegrationTestBase {

    @Autowired
    private TestDataService testDataService;
    @Autowired
    private CourseRecordRepository courseRecordRepository;

    @Test
    @Transactional
    public void testCompleteCourseRecordWithLearnerRecord() throws Exception {
        CourseRecord courseRecord = courseRecordRepository.save(testDataService.generateCourseRecord(0));
        String json = String.format("""
                {
                    "recordType": "COURSE",
                    "resourceId": "%s",
                    "learnerId": "%s",
                    "events": [
                        {
                            "eventType": "COMPLETE_COURSE",
                            "eventSource": "dummy",
                            "eventTimestamp": "2025-06-06T10:00:00"
                        }
                    ]
                }
                """, courseRecord.getCourseId(), courseRecord.getUserId());
        mockMvc.perform(post("/learner_records")
                        .with(csrf())
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isCreated());

        CourseRecord result = courseRecordRepository.getCourseRecord(courseRecord.getUserId(), courseRecord.getCourseId()).get();
        assertEquals(State.COMPLETED, result.getState());
        assertTime(result.getLastUpdated(), 6, 6, 2025, 10, 0, 0);
    }

    @Test
    @Transactional
    public void testCompleteCourseRecordWithLearnerRecordEvent() throws Exception {
        CourseRecord courseRecord = testDataService.generateCourseRecord(0);
        courseRecord.setIdentity(new CourseRecordIdentity("course1", "user1"));
        courseRecordRepository.save(courseRecord);
        String json = """
                [{
                    "resourceId": "course1",
                    "learnerId": "user1",
                    "eventType": "COMPLETE_COURSE",
                    "eventSource": "dummy",
                    "eventTimestamp": "2025-06-06T10:00:00"
                }]
                """;
        mockMvc.perform(post("/learner_record_events")
                        .with(csrf())
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isCreated());

        CourseRecord result = courseRecordRepository.getCourseRecord("user1", "course1").get();
        assertEquals(State.COMPLETED, result.getState());
        assertTime(result.getLastUpdated(), 6, 6, 2025, 10, 0, 0);
    }
}
