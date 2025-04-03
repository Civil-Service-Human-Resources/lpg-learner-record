package uk.gov.cslearning.record.integration.record;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.cslearning.record.IntegrationTestBase;
import uk.gov.cslearning.record.TestDataService;
import uk.gov.cslearning.record.repository.LearnerRecordEventRepository;
import uk.gov.cslearning.record.repository.LearnerRecordRepository;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LearnerRecordTest extends IntegrationTestBase {

    @Autowired
    private TestDataService testDataService;

    @Autowired
    private LearnerRecordRepository learnerRecordRepository;

    @Autowired
    private LearnerRecordEventRepository learnerRecordEventRepository;

    @Test
    public void testCreateLearnerRecord() throws Exception {
        String json = """
                {
                    "recordType": 1,
                    "resourceId": "course-id",
                    "learnerId": "user-id"
                }
                """;
        mockMvc.perform(post("/learner_records")
                        .with(csrf())
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    public void testCreateLearnerRecordWithEvents() throws Exception {
        String json = """
                {
                    "recordType": 1,
                    "resourceId": "course-id",
                    "learnerId": "user-id",
                    "events": [
                        {
                            "eventType": 1,
                            "eventSource": 1
                        }
                    ]
                }
                """;
        mockMvc.perform(post("/learner_records")
                        .with(csrf())
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    public void testCreateLearnerRecordWithChildRecord() throws Exception {
        String json = """
                {
                    "recordType": 1,
                    "resourceId": "course-id",
                    "learnerId": "user-id",
                    "children": [
                        {
                            "recordType": 2,
                            "resourceId": "module-id",
                            "learnerId": "user-id"
                        }
                    ]
                }
                """;
        mockMvc.perform(post("/learner_records")
                        .with(csrf())
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    public void testGetBulkRecords() throws Exception {
        String json = """
                {
                    "recordType": 1,
                    "resourceId": "course-id",
                    "learnerId": "user-id",
                    "children": [
                        {
                            "recordType": 2,
                            "resourceId": "module-id",
                            "learnerId": "user-id"
                        }
                    ]
                }
                """;
        mockMvc.perform(post("/learner_records")
                        .with(csrf())
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isCreated());
    }
}
