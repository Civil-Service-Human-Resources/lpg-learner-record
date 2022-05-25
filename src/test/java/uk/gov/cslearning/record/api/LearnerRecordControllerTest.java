package uk.gov.cslearning.record.api;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.service.UserRecordService;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class LearnerRecordControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private LearnerRecordController controller;

    @Mock
    private UserRecordService userRecordService;

    @Before
    public void setup() {
        initMocks(this);
        mockMvc = standaloneSetup(controller).build();
        ReflectionTestUtils.setField(controller, "learningLockerEnabled", true);
    }

    @Test
    public void shouldReturnEmptyListForInvalidUser() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/records/abc")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.records").isEmpty());
    }

    @Test
    public void shouldReturnRecords() throws Exception {

        when(userRecordService.getUserRecord("1", null))
                .thenReturn(ImmutableList.of(createRecord("abc", "1")));

        mockMvc.perform(
                MockMvcRequestBuilders.get("/records/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.records", hasSize(1)))
                .andExpect(jsonPath("$.records[0].courseId").value("abc"));
    }

    private CourseRecord createRecord(String courseId, String userId) {
        return new CourseRecord(courseId, userId);
    }
}
