package uk.gov.cslearning.record.integration.record;


import org.junit.jupiter.api.Test;
import uk.gov.cslearning.record.IntegrationTestBase;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LearnerRecordRecordEventTypeTest extends IntegrationTestBase {

    @Test
    public void testGetEventTypes() throws Exception {
        mockMvc.perform(get("/learner_record_event_types")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("length()").value("4"))
                .andExpect(jsonPath("[0].eventType").value("MOVE_TO_LEARNING_PLAN"))
                .andExpect(jsonPath("[0].description").value("Move a course to the learning plan, from suggested learning"))
                .andExpect(jsonPath("[0].learnerRecordType.type").value("COURSE"));
    }

}
