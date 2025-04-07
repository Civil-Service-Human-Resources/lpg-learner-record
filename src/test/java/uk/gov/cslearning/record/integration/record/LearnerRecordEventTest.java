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
public class LearnerRecordEventTest extends IntegrationTestBase {

    @Test
    public void testGetEventsForUser() throws Exception {
        mockMvc.perform(get("/learner_record_events")
                        .param("userId", "user2")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("content[0].eventType.eventType").value("MOVE_TO_LEARNING_PLAN"))
                .andExpect(jsonPath("content[1].eventType.eventType").value("REMOVE_FROM_SUGGESTIONS"))
                .andExpect(jsonPath("content[2].eventType.eventType").value("COMPLETE_COURSE"));
    }

    @Test
    public void testGetEventsForType() throws Exception {
        mockMvc.perform(get("/learner_record_events")
                        .param("eventTypes", "4")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("content.length()").value(3));
    }

    @Test
    public void testGetEventsForDates() throws Exception {
        mockMvc.perform(get("/learner_record_events")
                        .param("after", "2025-01-01T00:00")
                        .param("before", "2025-04-01T00:00")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("content.length()").value(1))
                .andExpect(jsonPath("content[0].eventType.eventType").value("MOVE_TO_LEARNING_PLAN"));
    }

    @Test
    public void testCreateEvents() throws Exception {
        String json = """
                [
                    {
                        "learnerRecordId": 5,
                        "eventType": 3,
                        "eventSource": 1
                    },
                    {
                        "learnerRecordId": 4,
                        "eventType": 4,
                        "eventSource": 1
                    },
                    {
                        "learnerRecordId": 110,
                        "eventType": 3,
                        "eventSource": 1
                    }
                ]
                """;
        mockMvc.perform(post("/learner_record_events")
                        .with(csrf())
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("successfulResources.length()").value(2))
                .andExpect(jsonPath("failedResources.length()").value(1))
                .andExpect(jsonPath("successfulResources[0].eventType.eventType").value("REMOVE_FROM_SUGGESTIONS"))
                .andExpect(jsonPath("successfulResources[1].eventType.eventType").value("COMPLETE_COURSE"))
                .andExpect(jsonPath("failedResources[0].resource.eventType").value(3))
                .andExpect(jsonPath("failedResources[0].reason").value("Learner record does not exist with  id: 110"));
    }

}
