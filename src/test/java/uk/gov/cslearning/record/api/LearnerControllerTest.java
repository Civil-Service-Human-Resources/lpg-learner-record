package uk.gov.cslearning.record.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.cslearning.record.dto.factory.ErrorDtoFactory;
import uk.gov.cslearning.record.service.LearnerService;

import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest({LearnerController.class, ErrorDtoFactory.class})
@WithMockUser(username = "user", authorities = "IDENTITY_DELETE")
public class LearnerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LearnerService learnerService;

    @Before
    public void setup() {
    }

    @Test
    public void shouldDeleteLearnerAndReturnNoContent() throws Exception {
        String uid = "test-learner-uid";

        mockMvc.perform(delete("/learner/" + uid).with(csrf()))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(learnerService).deleteLearnerByUid(uid);
    }
}