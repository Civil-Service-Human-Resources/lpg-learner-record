package uk.gov.cslearning.record.integration.record;


import org.junit.jupiter.api.Test;
import uk.gov.cslearning.record.IntegrationTestBase;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LearnerRecordRecordTypeTest extends IntegrationTestBase {

    @Test
    public void testGetLearnerRecordTypes() throws Exception {
        mockMvc.perform(get("/learner_record_types")
                        .param("includeEventTypes", "true")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].type").value("COURSE"))
                .andExpect(jsonPath("[0].validEventTypes[0].eventType").value("MOVE_TO_LEARNING_PLAN"))
                .andExpect(jsonPath("[0].validEventTypes[1].eventType").value("REMOVE_FROM_LEARNING_PLAN"))
                .andExpect(jsonPath("[0].validEventTypes[2].eventType").value("REMOVE_FROM_SUGGESTIONS"))
                .andExpect(jsonPath("[0].validEventTypes[3].eventType").value("COMPLETE_COURSE"));
    }

}
