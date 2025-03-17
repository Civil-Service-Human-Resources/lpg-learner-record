package uk.gov.cslearning.record.api;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.cslearning.record.MockedTestConfiguration;
import uk.gov.cslearning.record.SpringTestConfiguration;
import uk.gov.cslearning.record.service.LearnerService;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({LearnerController.class})
@Import({SpringTestConfiguration.class, MockedTestConfiguration.class})
@AutoConfigureMockMvc
@WithMockUser(username = "user", authorities = "IDENTITY_DELETE")
public class LearnerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LearnerService learnerService;

    @Test
    public void shouldDeleteLearnerAndReturnNoContent() throws Exception {
        String uid = "test-learner-uid";

        mockMvc.perform(delete("/learner/" + uid).with(csrf()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}
