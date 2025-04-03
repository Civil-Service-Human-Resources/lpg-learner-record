package uk.gov.cslearning.record.integration.record;


import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import uk.gov.cslearning.record.IntegrationTestBase;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class LearnerRecordTest extends IntegrationTestBase {

    @Test
    public void testGetLearnerRecord() throws Exception {
        mockMvc.perform(get("/learner_records/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("recordType.id").value(1))
                .andExpect(jsonPath("recordType.type").value("COURSE"))
                .andExpect(jsonPath("resourceId").value("course1"))
                .andExpect(jsonPath("learnerId").value("user1"));
    }

    @Test
    public void testGetLearnerRecordEvents() throws Exception {
        mockMvc.perform(get("/learner_records/1/events")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("content[0].eventType.eventType").value("MOVE_TO_LEARNING_PLAN"))
                .andExpect(jsonPath("content[1].eventType.eventType").value("REMOVE_FROM_LEARNING_PLAN"))
                .andExpect(jsonPath("content[2].eventType.eventType").value("REMOVE_FROM_SUGGESTIONS"))
                .andExpect(jsonPath("content[3].eventType.eventType").value("COMPLETE_COURSE"));
    }

    @Test
    public void testCreateLearnerRecord() throws Exception {
        String json = """
                {
                    "recordType": 1,
                    "resourceId": "course-id-123",
                    "learnerId": "user-id"
                }
                """;
        mockMvc.perform(post("/learner_records")
                        .with(csrf())
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("recordType.type").value("COURSE"))
                .andExpect(jsonPath("resourceId").value("course-id-123"))
                .andExpect(jsonPath("createdTimestamp").value("2023-01-01T10:00:00Z"))
                .andExpect(jsonPath("learnerId").value("user-id"));
    }

    @Test
    public void testCreateLearnerRecordWithEvents() throws Exception {
        String json = """
                {
                    "recordType": 1,
                    "resourceId": "course-id-456",
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
                .andExpect(status().isCreated())
                .andExpect(jsonPath("recordType.type").value("COURSE"))
                .andExpect(jsonPath("resourceId").value("course-id-456"))
                .andExpect(jsonPath("createdTimestamp").value("2023-01-01T10:00:00Z"))
                .andExpect(jsonPath("learnerId").value("user-id"))
                .andExpect(jsonPath("events[0].eventType.eventType").value("MOVE_TO_LEARNING_PLAN"))
                .andExpect(jsonPath("events[0].eventSource.source").value("CSL"));
    }

    @Test
    public void testCreateLearnerRecordWithChildRecord() throws Exception {
        String json = """
                {
                    "recordType": 1,
                    "resourceId": "course-id-789",
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
                .andExpect(status().isCreated())
                .andExpect(jsonPath("recordType.type").value("COURSE"))
                .andExpect(jsonPath("resourceId").value("course-id-789"))
                .andExpect(jsonPath("createdTimestamp").value("2023-01-01T10:00:00Z"))
                .andExpect(jsonPath("learnerId").value("user-id"))
                .andExpect(jsonPath("children[0].recordType.type").value("MODULE"))
                .andExpect(jsonPath("children[0].resourceId").value("module-id"))
                .andExpect(jsonPath("children[0].createdTimestamp").value("2023-01-01T10:00:00Z"))
                .andExpect(jsonPath("children[0].learnerId").value("user-id"));
    }

    @Test
    public void testCreateLearnerRecordEventsForRecord() throws Exception {
        String json = """
                [
                    {
                        "eventType": 1,
                        "eventSource": 1
                    },
                    {
                        "eventType": 3,
                        "eventSource": 1,
                        "eventTimestamp": "2025-06-01T12:00:00"
                    }
                ]
                """;
        mockMvc.perform(post("/learner_records/5/events")
                        .with(csrf())
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("[0].eventType.eventType").value("MOVE_TO_LEARNING_PLAN"))
                .andExpect(jsonPath("[0].eventSource.source").value("CSL"))
                .andExpect(jsonPath("[0].eventTimestamp").value("2023-01-01T10:00:00Z"))
                .andExpect(jsonPath("[1].eventType.eventType").value("REMOVE_FROM_SUGGESTIONS"))
                .andExpect(jsonPath("[1].eventSource.source").value("CSL"))
                .andExpect(jsonPath("[1].eventTimestamp").value("2025-06-01T11:00:00Z"));
    }

    @Test
    public void testGetLearnerRecords() throws Exception {
        mockMvc.perform(get("/learner_records")
                        .with(csrf())
                        .param("resourceId", "course1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("content[0].recordType.type").value("COURSE"))
                .andExpect(jsonPath("content[0].uid").value("course-record-uid6"))
                .andExpect(jsonPath("content[1].recordType.type").value("COURSE"))
                .andExpect(jsonPath("content[1].uid").value("course-record-uid4"))
                .andExpect(jsonPath("content[2].recordType.type").value("COURSE"))
                .andExpect(jsonPath("content[2].uid").value("course-record-uid1"));
    }
}
